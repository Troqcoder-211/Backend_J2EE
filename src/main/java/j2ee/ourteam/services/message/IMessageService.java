package j2ee.ourteam.services.message;

import java.util.UUID;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.interfaces.GenericCrudService;

public interface IMessageService extends GenericCrudService<Message, Object, Object, UUID> {

}
