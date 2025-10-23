package j2ee.ourteam.services.conversation;

import java.util.List;
import java.util.UUID;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.conversation.*;

public interface IConversationService extends GenericCrudService<Conversation, CreateConversationDTO, ConversationDTO, UUID> {

    ResponseDTO<List<ConversationDTO>> getAllConversation(User user);

    ResponseDTO<ConversationDTO> updateConversation(UUID id, UpdateConversationDTO dto, User user);

    ResponseDTO<Boolean> isArchived(UUID id, ArchivedConversationDTO dto, User user);

    ResponseDTO<ConversationDTO> createConversation(CreateConversationDTO dto, User user);

    ResponseDTO<Void> deleteConversationById(UUID uuid, User user);

    ResponseDTO<ConversationDTO> findConversationById(UUID uuid, User user);


}
