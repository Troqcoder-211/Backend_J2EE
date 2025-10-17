package j2ee.ourteam.services.conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.mapping.ConversationMapper;
import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import j2ee.ourteam.repositories.ConversationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ConversationServiceImpl implements IConversationService {
    private final ConversationMapper _conversationMapper;
    private final ConversationRepository _conversationRepository;

    public ConversationServiceImpl(ConversationMapper conversationMapper, ConversationRepository conversationRepository) {
        _conversationMapper = conversationMapper;
        _conversationRepository = conversationRepository;

    }

    @Override
    public List<ConversationDTO> findAll() {
        try{
            List<Conversation> conversationList = _conversationRepository.findAll();
            return conversationList.stream().map(_conversationMapper::toDto).toList();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred. Error: "+ e);
        }
    }

    @Override
    public Optional<ConversationDTO> findById(UUID uuid) {
        try{
            return _conversationRepository.findById(uuid).map(_conversationMapper::toDto);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred. Error: "+ e);
        }
    }

    @Override
    public ConversationDTO create(CreateConversationDTO dto) {
        try{
            System.out.println("=== BEFORE MAPPING ===");
            System.out.println("DTO: " + dto);
            System.out.println("DTO name: " + dto.getName());
            System.out.println("DTO conversationType: " + dto.getConversationType());

            Conversation conversation = _conversationMapper.toEntity(dto);

            System.out.println("=== AFTER MAPPING ===");
            System.out.println("Entity: " + conversation);
            System.out.println("Entity name: " + conversation.getName());
            System.out.println("Entity conversationType: " + conversation.getConversationType());
            System.out.println("======================");

            Conversation saved = _conversationRepository.save(conversation);
            return _conversationMapper.toDto(saved);
        } catch (Exception e){
            throw new RuntimeException("Error occurred. Error: "+ e);
        }
    }

    //tạm bỏ
    @Override
    public ConversationDTO update(UUID uuid, CreateConversationDTO dto) {
        return null;
    }

    @Transactional
    @Override
    public ConversationDTO update(UUID uuid, UpdateConversationDTO dto) {
        Optional<Conversation> conOpt = _conversationRepository.findById(uuid);
        if (conOpt.isEmpty()){
            throw new RuntimeException("Conversation not found with id: " + uuid);
        }
        try{
            Conversation conversation = conOpt.get();
            _conversationMapper.updateEntityFromDto(dto, conversation);
            Conversation updated = _conversationRepository.save(conversation);
            return _conversationMapper.toDto(updated);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred. Error: "+ e);
        }
    }

    @Override
    public Boolean isArchived(UUID id, ArchivedConversationDTO dto) {
        Optional<Conversation> conOpt = _conversationRepository.findById(id);
        if (conOpt.isEmpty()){
            throw new RuntimeException("Conversation not found with id: " + id);
        }
        try{
            Conversation conversation = conOpt.get();
            _conversationMapper.updateArchivedFromDto(dto, conversation);
            conversation.setIsArchived(true);
            Conversation isArchived = _conversationRepository.save(conversation);

            return isArchived.getIsArchived();
        }catch (Exception e) {
            throw new RuntimeException("Error occurred. Error: "+ e);
        }
    }

    @Transactional
    @Override
    public void deleteById(UUID uuid) {
        try{
            if (!_conversationRepository.existsById(uuid)){
                throw new RuntimeException("Conversation not found with id: "+uuid);
            }
            _conversationRepository.deleteById(uuid);
        }catch (Exception e) {
            throw new RuntimeException("Error occurred. Error: "+ e);
        }
    }
}
