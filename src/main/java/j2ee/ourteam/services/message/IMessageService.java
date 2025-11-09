package j2ee.ourteam.services.message;

import java.util.UUID;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.models.messagereaction.MessageReactionDTO;
import j2ee.ourteam.models.messageread.MessageReadDTO;

import org.springframework.data.domain.Page;

public interface IMessageService extends GenericCrudService<Message, Object, MessageDTO, UUID> {
  MessageDTO softDelete(UUID id);

  Page<MessageDTO> findAllPaged(MessageFilter filter);

  void addReaction(UUID id, CreateMessageReactionDTO messageReactionDTO);

  void deleteReaction(UUID id, UUID userId, String emoji);

  void markAsRead(UUID id, UUID userId);

  Page<MessageReadDTO> getReadStatus(UUID id, Integer page, Integer limit);

  Page<MessageReactionDTO> getReactions(UUID id, Integer page, Integer limit);

}
