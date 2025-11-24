package j2ee.ourteam.services.conversationmember;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMember.Role;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.ConversationMemberMapper;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation_member.AddConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.UpdateMuteDTO;
import j2ee.ourteam.models.conversation_member.UpdateRoleDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConversationMemberServiceImplTest extends BaseTest {

    private ConversationMemberMapper mapper;
    private ConversationRepository conversationRepo;
    private UserRepository userRepo;
    private ConversationMemberRepository memberRepo;
    private ConversationMemberServiceImpl service;

    @BeforeEach
    void setUp() {
        mapper = mock(ConversationMemberMapper.class);
        conversationRepo = mock(ConversationRepository.class);
        userRepo = mock(UserRepository.class);
        memberRepo = mock(ConversationMemberRepository.class);

        service = new ConversationMemberServiceImpl(mapper, conversationRepo, userRepo, memberRepo);
    }

    @Test
    void getMember_success() {
        UUID convId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Conversation conv = new Conversation();
        conv.setId(convId);
        conv.setCreatedBy(currentUser);

        when(userRepo.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(conversationRepo.findById(convId)).thenReturn(Optional.of(conv));
        when(memberRepo.findByIdConversationId(convId)).thenReturn(List.of(new ConversationMember()));
        when(mapper.toDto(any())).thenReturn(mock(j2ee.ourteam.models.conversation_member.ConversationMemberDTO.class));

        ResponseDTO<List<j2ee.ourteam.models.conversation_member.ConversationMemberDTO>> response =
                service.getMember(convId, currentUser);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
    }

    @Test
    void addMember_success() {
        UUID convId = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();

        User owner = new User();
        owner.setId(UUID.randomUUID());

        User newUser = new User();
        newUser.setId(newUserId);

        Conversation conv = new Conversation();
        conv.setId(convId);
        conv.setCreatedBy(owner);

        AddConversationMemberDTO dto = new AddConversationMemberDTO();
        dto.setUserId(newUserId);

        ConversationMember member = new ConversationMember();

        when(userRepo.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(userRepo.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(conversationRepo.findById(convId)).thenReturn(Optional.of(conv));
        when(memberRepo.existsById(new ConversationMemberId(convId, newUserId))).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(member);
        when(memberRepo.save(member)).thenReturn(member);
        when(mapper.toDto(member)).thenReturn(mock(j2ee.ourteam.models.conversation_member.ConversationMemberDTO.class));

        var response = service.addMember(convId, dto, owner);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void removeMember_success() {
        UUID convId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = new User();
        owner.setId(UUID.randomUUID());

        Conversation conv = new Conversation();
        conv.setId(convId);
        conv.setCreatedBy(owner);

        ConversationMember member = new ConversationMember();
        member.setId(new ConversationMemberId(convId, userId));

        when(userRepo.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(conversationRepo.findById(convId)).thenReturn(Optional.of(conv));
        when(memberRepo.findById(new ConversationMemberId(convId, userId))).thenReturn(Optional.of(member));

        var response = service.removeMember(convId, userId, owner);

        assertThat(response.isSuccess()).isTrue();
        verify(memberRepo).delete(member);
    }

    @Test
    void updateRole_success() {
        UUID convId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User owner = new User();
        owner.setId(UUID.randomUUID());

        Conversation conv = new Conversation();
        conv.setId(convId);
        conv.setCreatedBy(owner);

        ConversationMember member = new ConversationMember();
        member.setId(new ConversationMemberId(convId, userId));

        UpdateRoleDTO dto = new UpdateRoleDTO();
        dto.setRole(Role.MEMBER);

        when(userRepo.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(conversationRepo.findById(convId)).thenReturn(Optional.of(conv));
        when(memberRepo.findById(new ConversationMemberId(convId, userId))).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);
        when(mapper.toDto(member)).thenReturn(mock(j2ee.ourteam.models.conversation_member.ConversationMemberDTO.class));

        var response = service.updateRole(convId, userId, dto, owner);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void updateMute_success() {
        UUID convId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(userId);

        ConversationMember member = new ConversationMember();
        member.setId(new ConversationMemberId(convId, userId));

        UpdateMuteDTO dto = new UpdateMuteDTO();
        dto.setIsMuted(true);

        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
        when(memberRepo.findById(new ConversationMemberId(convId, userId))).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);
        when(mapper.toDto(member)).thenReturn(mock(j2ee.ourteam.models.conversation_member.ConversationMemberDTO.class));

        var response = service.updateMute(convId, userId, dto, currentUser);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    void leaveConversation_success() {
        UUID convId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(userId);

        ConversationMember member = new ConversationMember();
        member.setId(new ConversationMemberId(convId, userId));
        member.setRole(Role.MEMBER);

        when(userRepo.findById(userId)).thenReturn(Optional.of(currentUser));
        when(memberRepo.findById(new ConversationMemberId(convId, userId))).thenReturn(Optional.of(member));

        var response = service.leaveConversation(convId, currentUser);

        assertThat(response.isSuccess()).isTrue();
        verify(memberRepo).delete(member);
    }
}
