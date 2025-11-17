package j2ee.ourteam.services.conversationmember;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMember.Role;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.ConversationMemberMapper;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation_member.AddConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.UpdateMuteDTO;
import j2ee.ourteam.models.conversation_member.UpdateRoleDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConversationMemberServiceTest {

    @Mock
    private ConversationMemberMapper mapper;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversationMemberRepository memberRepository;

    @InjectMocks
    private ConversationMemberServiceImpl service;

    private User owner;
    private User member;
    private Conversation conversation;
    private UUID conversationId;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        owner = new User();
        owner.setId(UUID.randomUUID());

        member = new User();
        member.setId(UUID.randomUUID());

        conversationId = UUID.randomUUID();
        memberId = member.getId();

        conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setCreatedBy(owner);
    }

    @Test
    void testGetMember_UserNotFound() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.empty());
        ResponseDTO<List<ConversationMemberDTO>> result = service.getMember(conversationId, owner);
        assertFalse(result.isSuccess());
        assertEquals("User not found", result.getMessage());
    }

    @Test
    void testAddMember_Success() {
        AddConversationMemberDTO dto = new AddConversationMemberDTO();
        dto.setUserId(member.getId());

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberRepository.existsById(any())).thenReturn(false);

        ConversationMember savedMember = new ConversationMember();
        savedMember.setUser(member);
        savedMember.setConversation(conversation);

        when(mapper.toEntity(dto)).thenReturn(savedMember);
        when(memberRepository.save(savedMember)).thenReturn(savedMember);
        when(mapper.toDto(savedMember)).thenReturn(new ConversationMemberDTO());

        ResponseDTO<ConversationMemberDTO> result = service.addMember(conversationId, dto, owner);

        assertTrue(result.isSuccess());
        verify(memberRepository).save(savedMember);
    }

    @Test
    void testRemoveMember_Success() {
        ConversationMember memberEntity = new ConversationMember();
        memberEntity.setUser(member);
        memberEntity.setConversation(conversation);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(memberRepository.findById(new ConversationMemberId(conversationId, member.getId())))
                .thenReturn(Optional.of(memberEntity));

        ResponseDTO<Void> result = service.removeMember(conversationId, member.getId(), owner);

        assertTrue(result.isSuccess());
        verify(memberRepository).delete(memberEntity);
    }

    @Test
    void testUpdateRole_Success() {
        ConversationMember memberEntity = new ConversationMember();
        memberEntity.setUser(member);
        memberEntity.setConversation(conversation);

        UpdateRoleDTO dto = new UpdateRoleDTO();
        dto.setRole(Role.ADMIN);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(memberRepository.findById(new ConversationMemberId(conversationId, member.getId())))
                .thenReturn(Optional.of(memberEntity));

        when(mapper.toDto(any())).thenReturn(new ConversationMemberDTO());

        ResponseDTO<ConversationMemberDTO> result = service.updateRole(conversationId, member.getId(), dto, owner);

        assertTrue(result.isSuccess());
        verify(memberRepository).save(memberEntity);
    }

    @Test
    void testUpdateMute_Self_Success() {
        ConversationMember memberEntity = new ConversationMember();
        memberEntity.setUser(member);
        memberEntity.setConversation(conversation);

        UpdateMuteDTO dto = new UpdateMuteDTO();
        dto.setIsMuted(true);

        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberRepository.findById(new ConversationMemberId(conversationId, member.getId())))
                .thenReturn(Optional.of(memberEntity));
        when(mapper.toDto(memberEntity)).thenReturn(new ConversationMemberDTO());

        ResponseDTO<ConversationMemberDTO> result = service.updateMute(conversationId, member.getId(), dto, member);

        assertTrue(result.isSuccess());
        verify(memberRepository).save(memberEntity);
    }

    @Test
    void testLeaveConversation_Success() {
        ConversationMember memberEntity = new ConversationMember();
        memberEntity.setUser(member);
        memberEntity.setConversation(conversation);
        memberEntity.setRole(Role.MEMBER);

        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(memberRepository.findById(new ConversationMemberId(conversationId, member.getId())))
                .thenReturn(Optional.of(memberEntity));

        ResponseDTO<Void> result = service.leaveConversation(conversationId, member);

        assertTrue(result.isSuccess());
        verify(memberRepository).delete(memberEntity);
    }
}
