package j2ee.ourteam.services.conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.repositories.ConversationRepository;

@Service
public class ConversationServiceImpl implements IConversationService {

  private final ConversationRepository conversationRepository;

  public ConversationServiceImpl(ConversationRepository conversationRepository) {
    this.conversationRepository = conversationRepository;
  }

  @Override
  public List<Conversation> findAll() {
    return conversationRepository.findAll();
  }

  @Override
  public Optional<Conversation> findById(UUID id) {
    return conversationRepository.findById(id);
  }

  @Override
  public Conversation save(Conversation entity) {
    return conversationRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    conversationRepository.deleteById(id);
  }

}
