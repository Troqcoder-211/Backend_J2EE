package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  // Entity → DTO
  @Mapping(target = "conversationId", expression = "java(entity.getConversation() != null ? entity.getConversation().getId() : null)")
  @Mapping(target = "senderId", source = "sender.id")
  @Mapping(target = "replyTo", source = "replyTo")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "attachmentIds", ignore = true)
  MessageDTO toDto(Message entity);

  // DTO → Entity
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "conversation", ignore = true)
  @Mapping(target = "sender", ignore = true)
  @Mapping(target = "replyTo", ignore = true)
  @Mapping(target = "type", expression = "java(toEntityType(dto.getMessageType()))")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "editedAt", ignore = true)
  @Mapping(target = "isDeleted", constant = "false")
  @Mapping(target = "reactions", ignore = true)
  @Mapping(target = "reads", ignore = true)
  @Mapping(target = "attachments", ignore = true)
  Message toEntity(CreateMessageDTO dto);

  // Chuyển đổi enum hai chiều
  default Message.MessageType toEntityType(MessageTypeEnum enumValue) {
    return enumValue != null ? Message.MessageType.valueOf(enumValue.name()) : null;
  }

  default MessageTypeEnum toEnum(Message.MessageType enumValue) {
    return enumValue != null ? MessageTypeEnum.valueOf(enumValue.name()) : null;
  }
}
