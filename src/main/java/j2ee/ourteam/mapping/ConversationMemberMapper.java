package j2ee.ourteam.mapping;

import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.models.conversation_member.AddConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.UpdateMuteDTO;
import j2ee.ourteam.models.conversation_member.UpdateRoleDTO;
import org.mapstruct.Mapper;
import j2ee.ourteam.entities.ConversationMember.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ConversationMemberMapper {
    @Mapping(target = "userId", source = "id.userId")
    @Mapping(target = "conversationId", source = "id.conversationId")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "displayName", source = "user.displayName")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    @Mapping(target = "joinedAt", source = "joinedAt")
    @Mapping(target = "lastMessageId", source = "lastReadMessageId")
    @Mapping(target = "lastReadAt", source = "lastReadAt")
    ConversationMemberDTO toDto(ConversationMember entity);

    // Lưu ý: conversation và user cần set ở service vì không có trong DTO
    @Mapping(target = "id", source = "dto", qualifiedByName = "createIdFromAddDto")
    @Mapping(target = "role", source = "role", qualifiedByName = "stringToRole")
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    @Mapping(target = "isMuted", ignore = true)
    @Mapping(target = "lastReadMessageId", ignore = true)
    @Mapping(target = "lastReadAt", ignore = true)
    ConversationMember toEntity(AddConversationMemberDTO dto);

    default void updateFromRoleDto(UpdateRoleDTO dto, ConversationMember entity) {
        if (dto != null && entity != null) {
            entity.setRole(dto.getRole());
        }
    }

    default void updateFromMuteDto(UpdateMuteDTO dto, ConversationMember entity) {
        if (dto != null && entity != null) {
            entity.setIsMuted(dto.getIsMuted());
        }
    }

    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }

    @Named("stringToRole")
    default Role stringToRole(String roleStr) {
        if (roleStr == null || roleStr.isEmpty()) {
            return Role.MEMBER;  // Default nếu null
        }
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.MEMBER;
        }
    }

    @Named("createIdFromAddDto")
    default ConversationMemberId createIdFromAddDto(AddConversationMemberDTO dto) {
        return new ConversationMemberId(null, dto.getUserId());
    }
}
