package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.ConversationMember;

@Mapper(componentModel = "spring")
public interface ConversationMemberMapper {
  ConversationMember toDto(ConversationMember c);

  ConversationMember toEntity(ConversationMember c);
}
