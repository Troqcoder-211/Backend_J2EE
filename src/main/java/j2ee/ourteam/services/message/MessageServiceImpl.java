package j2ee.ourteam.services.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.repositories.MessageRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements IMessageService {

  private final MessageRepository messageRepository;

  private final MessageMapper messageMapper;
  // @Override
  // public List<MessageDTO> findAll() {
  // // return messageRepository.findAll();
  // }

  // @Override
  // public Optional<MessageDTO> findById(UUID id) {
  // // return messageRepository.findById(id);
  // }

  @Override
  public MessageDTO save(CreateMessageDTO model) {
    Message message = messageMapper.toEntity(model);

    // return messageRepository.save(model);
  }

  @Override
  public void deleteById(UUID id) {
    messageRepository.deleteById(id);
  }

}
