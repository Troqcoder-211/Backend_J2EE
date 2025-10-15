package j2ee.ourteam.mapping;

import j2ee.ourteam.models.conversation.ConversationDTO;
import org.mapstruct.*;

import j2ee.ourteam.entities.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    //entity->dto
    @Mapping(source = "id", target = "id")
    @Mapping(source = "conversationType", target = "conversationType")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "avatarS3Key", target = "avatarS3Key")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "isArchived", target = "isArchived")
    ConversationDTO toDto(Conversation entity);

    //dto->entity
    @Mapping(source = "id", target = "id")
    @Mapping(source = "conversationType", target = "conversationType")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "avatarS3Key", target = "avatarS3Key")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "isArchived", target = "isArchived")
    Conversation toEntity(ConversationDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "conversationType", target = "conversationType")
    @Mapping(source = "avatarS3Key", target = "avatarS3Key")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "isArchived", target = "isArchived")
    void updateEntityFromDto(ConversationDTO dto, @MappingTarget Conversation entity);
}
