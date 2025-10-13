package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;

@Mapper(componentModel = "spring")
public interface MessageMapper {

  @Mappings({
      @Mapping(target = "conversationId", source = "conversation.id"),
      @Mapping(target = "senderId", source = "sender.id"),
      @Mapping(target = "replyTo", source = "replyTo.id"),
      @Mapping(target = "messageType", source = "type")
  })
  MessageDTO toDto(Message entity);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "conversation", ignore = true),
      @Mapping(target = "sender", ignore = true),
      @Mapping(target = "replyTo", ignore = true),
      @Mapping(target = "type", source = "messageType"),
      @Mapping(target = "createdAt", expression = "java(java.time.LocalDate.now())"),
      @Mapping(target = "editedAt", ignore = true),
      @Mapping(target = "isDeleted", constant = "false"),
      @Mapping(target = "reactions", ignore = true),
      @Mapping(target = "reads", ignore = true),
      @Mapping(target = "attachments", ignore = true)
  })
  Message toEntity(CreateMessageDTO dto);

  // MapStruct sẽ tự dùng 2 hàm dưới nếu enum khác kiểu
  default Message.MessageType map(MessageTypeEnum enumValue) {
    return enumValue != null ? Message.MessageType.valueOf(enumValue.name()) : null;
  }

  default MessageTypeEnum map(Message.MessageType enumValue) {
    return enumValue != null ? MessageTypeEnum.valueOf(enumValue.name()) : null;
  }
}
