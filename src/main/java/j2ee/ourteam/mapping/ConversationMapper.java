package j2ee.ourteam.mapping;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import org.mapstruct.*;

import j2ee.ourteam.entities.Conversation;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { ConversationMemberMapper.class })
public interface ConversationMapper {

    // Map cơ bản Conversation → ConversationDTO
    @Mapping(target = "createdBy", expression = "java(entity.getCreatedBy() != null ? entity.getCreatedBy().getUserName() : null)")
    @Mapping(target = "lastMessage", ignore = true)
    ConversationDTO toDto(Conversation entity);

    // Map với currentUser để DM hiển thị đúng tên đối phương
    default ConversationDTO toDto(Conversation entity, User currentUser, ConversationMemberMapper memberMapper) {
        ConversationDTO dto = toDto(entity); // map các field cơ bản

        // Map members sang DTO nếu cần
        if (entity.getMembers() != null) {
            dto.setMembers(entity.getMembers().stream()
                    .map(memberMapper::toDto)
                    .collect(Collectors.toList()));
        }

        // DM case: set tên là tên đối phương
        if (entity.getConversationType() == Conversation.ConversationType.DM && dto.getMembers() != null) {
            entity.getMembers().stream()
                    .filter(m -> !m.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .ifPresent(m -> dto.setName(m.getUser().getUserName()));
        }

        // Group case: giữ nguyên tên cứng nếu có
        if (entity.getConversationType() != Conversation.ConversationType.DM) {
            dto.setName(entity.getName());
        }

        return dto;
    }

    // Map Create DTO → Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "messages", ignore = true)
    Conversation toEntity(CreateConversationDTO dto);

    // Update DTO → Entity
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversationType", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isArchived", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "messages", ignore = true)
    void updateEntityFromDto(UpdateConversationDTO dto, @MappingTarget Conversation entity);

    // Update isArchived
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
