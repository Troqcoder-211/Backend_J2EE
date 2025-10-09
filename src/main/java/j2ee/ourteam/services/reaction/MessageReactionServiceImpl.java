package j2ee.ourteam.services.reaction;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.MessageReaction;
import j2ee.ourteam.entities.MessageReactionId;
import j2ee.ourteam.repositories.MessageReactionRepository;

@Service
public class MessageReactionServiceImpl implements IMessageReactionService {

  private final MessageReactionRepository repo;

  public MessageReactionServiceImpl(MessageReactionRepository repo) {
    this.repo = repo;
  }

  @Override
  public List<MessageReaction> findAll() {
    return repo.findAll();
  }

  @Override
  public Optional<MessageReaction> findById(MessageReactionId id) {
    return repo.findById(id);
  }

  @Override
  public MessageReaction save(MessageReaction entity) {
    return repo.save(entity);
  }

  @Override
  public void deleteById(MessageReactionId id) {
    repo.deleteById(id);
  }

}
