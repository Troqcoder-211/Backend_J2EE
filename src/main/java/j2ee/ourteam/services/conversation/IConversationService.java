package j2ee.ourteam.services.conversation;

import java.util.UUID;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;

public interface IConversationService extends GenericCrudService<Conversation, CreateConversationDTO, ConversationDTO, UUID> {

}
