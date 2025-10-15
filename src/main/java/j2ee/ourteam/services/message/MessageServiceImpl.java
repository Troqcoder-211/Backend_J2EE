package j2ee.ourteam.services.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.UpdateMessageDTO;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.MessageRepository;
import j2ee.ourteam.repositories.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements IMessageService {
  private final MessageRepository messageRepository;
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
  public void softDelete(UUID id) {
    try {
      Message message = messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));

      message.setIsDeleted(true);

      messageRepository.save(message);
    } catch (Exception e) {
      throw new RuntimeException("Failed to soft delete  messge" + e.getMessage(), e);
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
  public Page<MessageDTO> findAllPaged(Pageable pageable) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAllPaged'");
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

}
