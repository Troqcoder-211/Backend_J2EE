package j2ee.ourteam.mapping;

import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import org.mapstruct.*;

import j2ee.ourteam.entities.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(target = "createdBy", expression = "java(entity.getCreatedBy() != null ? entity.getCreatedBy().getUserName() : null)")
    ConversationDTO toDto(Conversation entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "messages", ignore = true)
    Conversation toEntity(CreateConversationDTO dto);


    //Update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversationType", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "messages", ignore = true)
    void updateEntityFromDto(UpdateConversationDTO dto, @MappingTarget Conversation entity);

    //isArchived
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "avatarS3Key", ignore = true)
    @Mapping(target = "conversationType", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "messages", ignore = true)
    void updateArchivedFromDto(ArchivedConversationDTO dto, @MappingTarget Conversation entity);


}