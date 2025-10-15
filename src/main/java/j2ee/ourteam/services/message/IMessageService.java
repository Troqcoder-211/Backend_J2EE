package j2ee.ourteam.services.message;

import java.util.UUID;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;

import org.springframework.data.domain.Page;

public interface IMessageService extends GenericCrudService<Message, Object, MessageDTO, UUID> {
  MessageDTO softDelete(UUID id);

  Page<MessageDTO> findAllPaged(MessageFilter filter);
}
