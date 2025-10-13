package j2ee.ourteam.services.message;

import java.util.UUID;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;

public interface IMessageService extends GenericCrudService<Message, CreateMessageDTO, MessageDTO, UUID> {

}
