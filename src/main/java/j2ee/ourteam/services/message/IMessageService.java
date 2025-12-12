package j2ee.ourteam.services.message;

import java.util.UUID;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.models.messagereaction.MessageReactionDTO;
import j2ee.ourteam.models.messageread.MessageReadDTO;
import j2ee.ourteam.models.message.CreateReplyMessageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMessageService extends GenericCrudService<Message, Object, MessageDTO, UUID> {
  MessageDTO softDelete(UUID id);

  Page<MessageDTO> findAllPaged(MessageFilter filter);

  void addReaction(UUID id, CreateMessageReactionDTO messageReactionDTO);

  void deleteReaction(UUID id, UUID userId, String emoji);

  Page<MessageReadDTO> markConversationAsRead(UUID conversationId, UUID userId, Pageable pageable);

  Page<MessageReadDTO> getMessageReaders(UUID messageId, Pageable pageable);

  Page<MessageReactionDTO> getReactions(UUID id, Integer page, Integer limit);

  MessageDTO reply(CreateReplyMessageDTO dto);

}
