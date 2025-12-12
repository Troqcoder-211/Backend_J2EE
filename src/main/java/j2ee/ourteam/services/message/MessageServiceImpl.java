package j2ee.ourteam.services.message;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import j2ee.ourteam.entities.*;
import j2ee.ourteam.mapping.AttachmentMapper;
import j2ee.ourteam.models.messagereaction.MessageReactionDTO;
import j2ee.ourteam.repositories.*;
import org.hibernate.Hibernate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import j2ee.ourteam.controllers.WebSocketController;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.attachment.AttachmentDTO;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.CreateReplyMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;
import j2ee.ourteam.models.message.MessageSpecification;
import j2ee.ourteam.models.message.UpdateMessageDTO;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.models.messageread.MessageReadDTO;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements IMessageService {
  private final MessageRepository messageRepository;
  private final MessageReactionRepository messageReactionRepository;
  private final MessageReadRepository messageReadRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationMemberRepository conversationMemberRepository;
  private final AttachmentRepository attachmentRepository;
  private final UserRepository userRepository;

  private final MessageMapper messageMapper;
  private final AttachmentMapper attachmentMapper;

  @Autowired
  private WebSocketController webSocketController;

  private static final String ERROR_EMPTY_CONTENT = "Content cannot be empty for text messages";
  private static final String ERROR_EMPTY_ATTACHMENTS = "Attachments are required for non-text messages";

  // Class MessageServiceImple.java (hoặc tương tự)

  @Override
  @Transactional(readOnly = true)
  public Page<MessageDTO> findAllPaged(MessageFilter filter) {
    filter.normalize(); // chuẩn hóa sortOrder, sortBy, page, limit

    try {
      // === Chuẩn hóa Sort Order ===
      Sort.Direction direction;
      try {
        direction = Sort.Direction.fromString(filter.getSortOrder());
      } catch (Exception ex) {
        direction = Sort.Direction.DESC; // fallback an toàn
      }

      // === Validate sortBy có tồn tại trong entity không ===
      if (!isValidSortField(filter.getSortBy())) {
        throw new IllegalArgumentException("Invalid sortBy field: " + filter.getSortBy());
      }

      Pageable pageable = PageRequest.of(
          Math.max(filter.getPage() - 1, 0),
          filter.getLimit(),
          Sort.by(direction, filter.getSortBy()));

      Page<Message> page = messageRepository.findAll(
          MessageSpecification.filter(filter),
          pageable);

      return page.map(message -> {
        Set<Attachment> attachments = message.getAttachments();
        Set<AttachmentDTO> attachmentDTOs = attachments.stream()
            .map(attachmentMapper::toDto).collect(Collectors.toSet());

        // 💡 Map ReplyTo (Sử dụng MessageMapper để map đệ quy Message -> MessageDTO)
        MessageDTO replyToDto = (message.getReplyTo() != null)
            ? messageMapper.toDto(message.getReplyTo())
            : null;

        return MessageDTO.builder()
            .id(message.getId())
            .content(message.getContent())
            .type(messageMapper.toEnum(message.getType()))
            .createdAt(message.getCreatedAt())
            .editedAt(message.getEditedAt())
            .isDeleted(message.getIsDeleted())

            .senderId(message.getSender() != null ? message.getSender().getId() : null)
            .conversationId(message.getConversation() != null ? message.getConversation().getId() : null)

            .attachments(attachmentDTOs)
            .replyTo(replyToDto)
            .build();
      });

    } catch (Exception e) {
      // Giữ nguyên stack trace cực kỳ quan trọng
      throw new RuntimeException("Failed to findAllPaged: " + e.getMessage(), e);
    }
  }

  // Hàm kiểm tra sortBy hợp lệ
  private boolean isValidSortField(String field) {
    return Arrays.stream(Message.class.getDeclaredFields())
        .anyMatch(f -> f.getName().equals(field));
  }

  @Override
  @Transactional
  public MessageDTO create(Object dto) {
    if (!(dto instanceof CreateMessageDTO createDto)) {
      throw new IllegalArgumentException("Invalid DTO type for create");
    }

    // Validate input
    validateMessageInput(createDto);

    // Lấy entities
    Conversation conversation = findConversation(createDto.getConversationId());
    User sender = findSender(createDto.getSenderId());
    Message replyTo = createDto.getReplyTo() != null ? findReplyMessage(createDto.getReplyTo()) : null;

    // Map DTO → entity
    Message message = messageMapper.toEntity(createDto);
    message.setConversation(conversation);
    message.setSender(sender);
    message.setReplyTo(replyTo);

    // Gắn attachments nếu có
    Set<AttachmentDTO> attachmentsDtoSet = null;

    if (createDto.getAttachmentIds() != null && !createDto.getAttachmentIds().isEmpty()) {
      List<Attachment> attachments = attachmentRepository.findAllById(createDto.getAttachmentIds());

      if (attachments.size() != createDto.getAttachmentIds().size()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some attachments not found");
      }

      message.setAttachments(new HashSet<>(attachments));

      // Map Attachment → AttachmentDTO
      attachmentsDtoSet = attachments.stream()
          .map(a -> AttachmentDTO.builder()
              .id(a.getId())
              .uploaderId(a.getUploader().getId())
              .conversationId(a.getConversation().getId())
              .filename(a.getFilename())
              .mimeType(a.getMimeType())
              .sizeBytes(a.getSizeBytes())
              .s3Key(a.getS3Key())
              .thumbnailS3Key(a.getThumbnailS3Key())
              .build())
          .collect(Collectors.toSet());
    }

    // Lưu message
    Message saved = messageRepository.save(message);

    // After saving
    MessageDTO result = messageMapper.toDto(saved);

    if (attachmentsDtoSet != null) {
      result.setAttachments(attachmentsDtoSet);
    }

    // Push websocket
    webSocketController.sendMessage(result);

    return result;
  }

  @Override
  @Transactional
  public MessageDTO reply(CreateReplyMessageDTO dto) {
    if (dto == null)
      throw new IllegalArgumentException("DTO cannot be null");

    // Lấy entities
    Conversation conversation = findConversation(dto.getConversationId());
    User sender = findSender(dto.getSenderId());
    Message original = findReplyMessage(dto.getReplyToMessageId());

    // Tạo message trả lời
    Message reply = Message.builder()
        .conversation(conversation)
        .sender(sender)
        .content(dto.getContent())
        .type(Message.MessageType.REPLY)
        .replyTo(original)
        .build();

    // Gắn attachments nếu có
    Set<AttachmentDTO> attachmentsDtoSet = null;
    if (dto.getAttachmentIds() != null && !dto.getAttachmentIds().isEmpty()) {
      List<Attachment> attachments = attachmentRepository.findAllById(dto.getAttachmentIds());
      if (attachments.size() != dto.getAttachmentIds().size()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some attachments not found");
      }
      reply.setAttachments(new HashSet<>(attachments));

      attachmentsDtoSet = attachments.stream()
          .map(a -> AttachmentDTO.builder()
              .id(a.getId())
              .filename(a.getFilename())
              .mimeType(a.getMimeType())
              .sizeBytes(a.getSizeBytes())
              .s3Key(a.getS3Key())
              .thumbnailS3Key(a.getThumbnailS3Key())
              .build())
          .collect(Collectors.toSet());
    }

    // Lưu message
    Message saved = messageRepository.save(reply);

    // Mark sender has read
    messageReadRepository.insertMessageRead(saved.getId(), sender.getId(), LocalDateTime.now());

    // After saving
    MessageDTO result = messageMapper.toDto(saved);
    if (attachmentsDtoSet != null) {
      result.setAttachments(attachmentsDtoSet);
    }

    // Push websocket
    webSocketController.sendMessage(result);

    return result;
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

      Message saved = messageRepository.save(message);

      MessageDTO result = messageMapper.toDto(saved);

      webSocketController.updateMessage(result);

      return result;
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
      Message saved = messageRepository.save(message);

      MessageDTO result = messageMapper.toDto(saved);

      webSocketController.updateMessage(result);

      return result;
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

      if (!messageReactionRepository.existsById(key))
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reaction not found");

      messageReactionRepository.deleteById(key);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete reaction message" + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public Page<MessageReadDTO> markConversationAsRead(UUID conversationId, UUID userId, Pageable pageable) {

    Message lastMessage = messageRepository
        .findTopByConversationIdOrderByCreatedAtDesc(conversationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation is empty"));

    ConversationMember member = conversationMemberRepository
        .findByConversationIdAndUserId(conversationId, userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not in conversation"));

    member.setLastReadMessageId(lastMessage.getId());
    member.setLastReadAt(lastMessage.getCreatedAt());
    conversationMemberRepository.save(member);

    List<UUID> unreadMessageIds = messageReadRepository
        .findUnreadMessageIds(conversationId, userId, lastMessage.getCreatedAt());

    LocalDateTime now = LocalDateTime.now();
    for (UUID messageId : unreadMessageIds) {
      messageReadRepository.insertMessageRead(messageId, userId, now);
    }

    return messageReadRepository.findByMessageIdAsDTO(lastMessage.getId(), pageable);
  }

  @Override
  public Page<MessageReadDTO> getMessageReaders(UUID messageId, Pageable pageable) {
    return messageReadRepository.findByMessageIdAsDTO(messageId, pageable);
  }

  @Override
  public Page<MessageReactionDTO> getReactions(UUID id, Integer page, Integer limit) {
    try {
      Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("reactedAt").descending());

      return messageReactionRepository.findByMessageId(id, pageable)
          .map(reaction -> MessageReactionDTO.builder()
              .messageId(reaction.getId().getMessageId())
              .userId(reaction.getUser().getId())
              .avatar(reaction.getUser().getAvatarS3Key())
              .username(reaction.getUser().getUserName())
              .emoji(reaction.getId().getEmoji())
              .createdAt(reaction.getReactedAt())
              .build());

    } catch (Exception e) {
      throw new RuntimeException("Failed to get reactions message" + e.getMessage(), e);
    }

  }

}
