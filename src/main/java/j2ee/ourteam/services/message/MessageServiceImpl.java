package j2ee.ourteam.services.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.MessageReaction;
import j2ee.ourteam.entities.MessageReactionId;
import j2ee.ourteam.entities.MessageRead;
import j2ee.ourteam.entities.MessageReadId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;
import j2ee.ourteam.models.message.MessageSpecification;
import j2ee.ourteam.models.message.UpdateMessageDTO;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.models.messagereaction.MessageReactionDTO;
import j2ee.ourteam.models.messageread.MessageReadDTO;
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
  private final UserRepository userRepository;

  private final MessageMapper messageMapper;

  @Override
  public MessageDTO create(Object dto) {
    if (!(dto instanceof CreateMessageDTO createDto)) {
      throw new IllegalArgumentException("Invalid DTO type for create");
    }

    try {
      Message message = messageMapper.toEntity(createDto);

      Conversation conversation = conversationRepository.findById(createDto.getConversationId())
          .orElseThrow(() -> new RuntimeException("Conversation not found"));

      message.setConversation(conversation);

      User sender = userRepository.findById(createDto.getSenderId())
          .orElseThrow(() -> new RuntimeException("Sender not found"));
      message.setSender(sender);

      if (createDto.getReplyTo() != null) {
        Message replyTo = messageRepository.findById(createDto.getReplyTo())
            .orElseThrow(() -> new RuntimeException("Reply message not found"));
        message.setReplyTo(replyTo);
      }

      messageRepository.save(message);

      // Save data
      return messageMapper.toDto(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create message: " + e.getMessage(), e);
    }
  }

  @Override
  public MessageDTO update(UUID id, Object dto) {
    if (!(dto instanceof UpdateMessageDTO updateDto)) {
      throw new IllegalArgumentException("Invalid DTO type for update");
    }

    try {
      Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));

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
          .orElseThrow(() -> new RuntimeException("Message not found"));

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
    Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));

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
          .orElseThrow(() -> new RuntimeException("Message not found"));

      User user = userRepository.findById(messageReactionDTO.getUserId())
          .orElseThrow(() -> new RuntimeException("User not found"));

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

      if (messageReactionRepository.existsById(key)) {
        throw new RuntimeException("Reaction not found");
      }

      messageReactionRepository.deleteById(key);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete reaction message" + e.getMessage(), e);
    }
  }

  @Override
  public void markAsRead(UUID id, UUID userId) {
    try {
      Message message = messageRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Message not found"));

      User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found"));

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
