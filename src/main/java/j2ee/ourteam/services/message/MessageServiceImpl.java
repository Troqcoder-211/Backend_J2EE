package j2ee.ourteam.services.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.repositories.MessageRepository;

@Service
public class MessageServiceImpl implements IMessageService {

  private final MessageRepository messageRepository;

  public MessageServiceImpl(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Override
  public List<Message> findAll() {
    return messageRepository.findAll();
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return messageRepository.findById(id);
  }

  @Override
  public Message save(Message entity) {
    return messageRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    messageRepository.deleteById(id);
  }

}
