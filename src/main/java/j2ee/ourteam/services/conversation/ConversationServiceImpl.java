package j2ee.ourteam.services.conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.repositories.ConversationRepository;

@Service
public class ConversationServiceImpl implements IConversationService {

    @Override
    public List<ConversationDTO> findAll() {
        return List.of();
    }

    @Override
    public Optional<ConversationDTO> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public ConversationDTO create(CreateConversationDTO dto) {
        return null;
    }

    @Override
    public ConversationDTO update(UUID uuid, CreateConversationDTO dto) {
        return null;
    }

    @Override
    public void deleteById(UUID uuid) {

    }
}
