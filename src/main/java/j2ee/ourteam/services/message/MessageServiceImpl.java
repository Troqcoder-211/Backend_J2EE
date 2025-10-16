package j2ee.ourteam.services.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import j2ee.ourteam.entities.Attachment;
import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.MessageReaction;
import j2ee.ourteam.entities.MessageReactionId;
import j2ee.ourteam.entities.MessageRead;
import j2ee.ourteam.entities.MessageReadId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;
import j2ee.ourteam.models.message.MessageSpecification;
import j2ee.ourteam.models.message.UpdateMessageDTO;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.models.messageread.MessageReadDTO;
import j2ee.ourteam.repositories.AttachmentRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.MessageReactionRepository;
import j2ee.ourteam.repositories.MessageReadRepository;
import j2ee.ourteam.repositories.MessageRepository;
import j2ee.ourteam.repositories.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements IMessageService {
  private final MessageRepository messageRepository;
  private final MessageReactionRepository messageReactionRepository;
  private final MessageReadRepository messageReadRepository;
  private final ConversationRepository conversationRepository;
  private final AttachmentRepository attachmentRepository;
  private final UserRepository userRepository;

  private final MessageMapper messageMapper;

  private static final String ERROR_EMPTY_CONTENT = "Content cannot be empty for text messages";
  private static final String ERROR_EMPTY_ATTACHMENTS = "Attachments are required for non-text messages";

  @Override
  public MessageDTO create(Object dto) {
    if (!(dto instanceof CreateMessageDTO createDto)) {
      throw new IllegalArgumentException("Invalid DTO type for create");
    }

    validateMessageInput(createDto);

    Conversation conversation = findConversation(createDto.getConversationId());
    User sender = findSender(createDto.getSenderId());
    Message replyTo = findReplyMessage(createDto.getReplyTo());

    Message message = messageMapper.toEntity(createDto);
    message.setConversation(conversation);
    message.setSender(sender);
    message.setReplyTo(replyTo);

    attachFiles(message, createDto.getAttachmentIds());

    Message saved = messageRepository.save(message);

    return messageMapper.toDto(saved);
  }

  private void validateMessageInput(CreateMessageDTO dto) {
    boolean isText = dto.getMessageType() == MessageTypeEnum.TEXT;

    if (isText && (dto.getContent() == null || dto.getContent().isBlank())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_EMPTY_CONTENT);
    }

    if (!isText && (dto.getAttachmentIds() == null || dto.getAttachmentIds().isEmpty())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ERROR_EMPTY_ATTACHMENTS);
    }
  }

  private Conversation findConversation(UUID conversationId) {
    return conversationRepository.findById(conversationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));
  }

  private User findSender(UUID senderId) {
    return userRepository.findById(senderId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
  }

  private Message findReplyMessage(UUID replyToId) {
    if (replyToId == null)
      return null;
    return messageRepository.findById(replyToId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply message not found"));
  }

  private void attachFiles(Message message, List<UUID> attachmentIds) {
    if (attachmentIds == null || attachmentIds.isEmpty())
      return;

    List<Attachment> attachments = attachmentRepository.findAllById(attachmentIds);
    if (attachments.size() != attachmentIds.size()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some attachments not found");
    }

    message.getAttachments().addAll(attachments);
  }

  @Override
  public MessageDTO update(UUID id, Object dto) {
    if (!(dto instanceof UpdateMessageDTO updateDto)) {
      throw new IllegalArgumentException("Invalid DTO type for update");
    }

    try {
      Message message = messageRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

      message.setContent(updateDto.getContent());
      message.setEditedAt(updateDto.getEditedAt());

      messageRepository.save(message);

      return messageMapper.toDto(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to update message" + e.getMessage(), e);
    }
  }

  @Override
  public MessageDTO softDelete(UUID id) {
    try {
      Message message = messageRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

      if (Boolean.TRUE.equals(message.getIsDeleted())) {
        throw new RuntimeException("Message is already deleted");
      }

      message.setIsDeleted(true);
      messageRepository.save(message);

      return messageMapper.toDto(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to soft delete message: " + e.getMessage(), e);
    }
  }

  @Override
  public void deleteById(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

    try {
      messageRepository.delete(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete messge" + e.getMessage(), e);
    }
  }

  @Override
  public Page<MessageDTO> findAllPaged(MessageFilter filter) {
    filter.normalize();

    try {
      Pageable pageable = PageRequest.of(
          filter.getPage() - 1,
          filter.getLimit(),
          Sort.by(Sort.Direction.fromString(filter.getSortOrder()), filter.getSortBy()));

      return messageRepository.findAll(MessageSpecification.filter(filter), pageable)
          .map(messageMapper::toDto);
    } catch (Exception e) {
      throw new RuntimeException("Failed to findAllPaged" + e.getMessage(), e);
    }
  }

  @Override
  public Optional<MessageDTO> findById(UUID id) {
    try {

      return messageRepository.findById(id).map(messageMapper::toDto);

    } catch (Exception e) {
      throw new RuntimeException("Failed to findById messge" + e.getMessage(), e);
    }
  }

  @Override
  public List<MessageDTO> findAll() {
    try {

      return messageRepository.findAll().stream().map(messageMapper::toDto).toList();

    } catch (Exception e) {
      throw new RuntimeException("Failed to findById messge" + e.getMessage(), e);
    }
  }

  @Override
  public void addReaction(UUID id, CreateMessageReactionDTO messageReactionDTO) {
    try {
      Message message = messageRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

      User user = userRepository.findById(messageReactionDTO.getUserId())
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

      MessageReactionId key = new MessageReactionId(id, messageReactionDTO.getUserId(), messageReactionDTO.getEmoji());

      if (messageReactionRepository.existsById(key)) {
        throw new RuntimeException("Reaction already exists");
      }

      MessageReaction reaction = MessageReaction.builder()
          .id(key)
          .message(message)
          .user(user)
          .build();

      messageReactionRepository.save(reaction);

    } catch (Exception e) {
      throw new RuntimeException("Failed to add reaction message" + e.getMessage(), e);
    }
  }

  @Override
  public void deleteReaction(UUID id, UUID userId, String emoji) {
    try {
      MessageReactionId key = new MessageReactionId(id, userId, emoji);

      if (messageReactionRepository.existsById(key))
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found");

      messageReactionRepository.deleteById(key);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete reaction message" + e.getMessage(), e);
    }
  }

  @Override
  public void markAsRead(UUID id, UUID userId) {
    try {
      Message message = messageRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

      MessageReadId key = new MessageReadId(id, userId);

      if (messageReadRepository.existsById(key)) {
        throw new RuntimeException("Read already exist");
      }

      MessageRead messageRead = MessageRead
          .builder()
          .id(key)
          .message(message)
          .user(user)
          .build();

      messageReadRepository.save(messageRead);
    } catch (Exception e) {
      throw new RuntimeException("Failed to mark As Read Message" + e.getMessage(), e);
    }

  }

  @Override
  public Page<MessageReadDTO> getReadStatus(UUID id, Integer page, Integer limit) {
    try {
      Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("readAt").descending());

      return messageReadRepository.findByMessageId(id, pageable)
          .map(read -> MessageReadDTO.builder()
              .userId(read.getUser().getId())
              .username(read.getUser().getUserName())
              .username(read.getUser().getAvatarS3Key())
              .readAt(read.getReadAt())
              .build());

    } catch (Exception e) {
      throw new RuntimeException("Failed to get Read Status message" + e.getMessage(), e);
    }
  }

}
