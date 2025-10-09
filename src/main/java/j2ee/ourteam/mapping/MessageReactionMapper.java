package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.MessageReaction;

@Mapper(componentModel = "spring")
public interface MessageReactionMapper {
  MessageReaction toDto(MessageReaction m);

  MessageReaction toEntity(MessageReaction m);
}
