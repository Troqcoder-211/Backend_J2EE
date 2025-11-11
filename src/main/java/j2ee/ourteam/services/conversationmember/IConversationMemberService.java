package j2ee.ourteam.services.conversationmember;

import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation_member.*;

import java.util.List;
import java.util.UUID;

public interface IConversationMemberService
        extends
        GenericCrudService<ConversationMember, AddConversationMemberDTO, ConversationMemberDTO, ConversationMemberId> {
    ResponseDTO<List<ConversationMemberDTO>> getMember(UUID conversationId, User user);

    ResponseDTO<ConversationMemberDTO> addMember(UUID conversationId, AddConversationMemberDTO dto, User user);

    ResponseDTO<ConversationMemberDTO> updateRole(UUID conversationId, UUID userId, UpdateRoleDTO dto, User user);

    ResponseDTO<ConversationMemberDTO> updateMute(UUID conversationId, UUID userId, UpdateMuteDTO dto, User user);

    ResponseDTO<Void> removeMember(UUID conversationId, UUID userId, User user);

    ResponseDTO<Void> leaveConversation(UUID conversationId, User currentUser);
}
