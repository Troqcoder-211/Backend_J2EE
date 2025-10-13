package j2ee.ourteam.services.conversation;

import java.util.UUID;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.interfaces.GenericCrudService;

public interface IConversationService extends GenericCrudService<Conversation, Object, Object, UUID> {

}
