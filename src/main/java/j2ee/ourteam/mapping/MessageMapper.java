package j2ee.ourteam.mapping;

import j2ee.ourteam.models.message.MessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import j2ee.ourteam.entities.Message;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "senderId", ignore = true)
    @Mapping(target = "conversationId", ignore = true)
    @Mapping(target = "attachmentIds", ignore = true)
    MessageDTO toDto(Message m);

    Message toEntity(MessageDTO dto);

    // Custom method to map Message object to UUID
    default UUID mapMessageToUuid(Message message) {
        return message != null ? message.getId() : null;
    }

    // Custom method to map UUID to Message object (if needed)
    default Message mapUuidToMessage(UUID id) {
        if (id == null) return null;
        Message message = new Message();
        message.setId(id);
        return message;
    }
}