package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
  Conversation toDto(Conversation c);

  Conversation toEntity(Conversation c);
}
