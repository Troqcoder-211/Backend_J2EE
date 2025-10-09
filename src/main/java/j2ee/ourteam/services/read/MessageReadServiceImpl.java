package j2ee.ourteam.services.read;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.MessageRead;
import j2ee.ourteam.entities.MessageReadId;
import j2ee.ourteam.repositories.MessageReadRepository;

@Service
public class MessageReadServiceImpl implements IMessageReadService {

  private final MessageReadRepository repo;

  public MessageReadServiceImpl(MessageReadRepository repo) {
    this.repo = repo;
  }

  @Override
  public List<MessageRead> findAll() {
    return repo.findAll();
  }

  @Override
  public Optional<MessageRead> findById(MessageReadId id) {
    return repo.findById(id);
  }

  @Override
  public MessageRead save(MessageRead entity) {
    return repo.save(entity);
  }

  @Override
  public void deleteById(MessageReadId id) {
    repo.deleteById(id);
  }

}
