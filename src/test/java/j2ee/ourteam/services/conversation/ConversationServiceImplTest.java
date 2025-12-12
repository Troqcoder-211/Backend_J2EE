package j2ee.ourteam.services.conversation;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.ConversationMapper;
import j2ee.ourteam.mapping.ConversationMemberMapper;
import j2ee.ourteam.models.conversation.*;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.redis.ConversationSoftDeleteService;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConversationServiceImplTest extends BaseTest {

    private ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private ConversationMemberRepository conversationMemberRepository;
    private ConversationMapper conversationMapper;
    private ConversationMemberMapper conversationMemberMapper;
    private ConversationSoftDeleteService conversationSoftDeleteService;
    private ConversationServiceImpl service;

    @BeforeEach
    void setUp() {
        conversationRepository = mock(ConversationRepository.class);
        userRepository = mock(UserRepository.class);
        conversationMemberRepository = mock(ConversationMemberRepository.class);
        conversationMapper = mock(ConversationMapper.class);
        conversationMemberMapper = mock(ConversationMemberMapper.class);
        conversationSoftDeleteService = mock(ConversationSoftDeleteService.class);

        service = new ConversationServiceImpl(
                conversationMapper,
                conversationMemberMapper,
                conversationRepository,
                userRepository,
                conversationMemberRepository,
                conversationSoftDeleteService
        );
    }

    @Test
    void createConversation_DM_existingConversation_restores() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        CreateConversationDTO dto = new CreateConversationDTO();
        dto.setConversationType(Conversation.ConversationType.DM);
        ConversationMemberDTO memberDto = new ConversationMemberDTO();
        UUID otherUserId = UUID.randomUUID();
        memberDto.setUserId(otherUserId);
        dto.setMembers(List.of(memberDto));

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));

        Conversation existingConversation = new Conversation();
        existingConversation.setId(UUID.randomUUID());
        existingConversation.setMembers(new ArrayList<>());

        when(conversationMemberRepository.findExistingDm(currentUser.getId(), otherUserId))
                .thenReturn(Optional.of(existingConversation));

        when(conversationSoftDeleteService.isDeleted(currentUser.getId().toString(), existingConversation.getId().toString()))
                .thenReturn(true);

        when(conversationRepository.save(existingConversation)).thenReturn(existingConversation);
        when(conversationMapper.toDto(existingConversation, currentUser, conversationMemberMapper))
                .thenReturn(new ConversationDTO());

        var response = service.createConversation(dto, currentUser);

        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        verify(conversationSoftDeleteService).restore(currentUser.getId().toString(), existingConversation.getId().toString());
    }

    @Test
    void updateConversation_onlyName_success() {
        UUID convId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Conversation conversation = new Conversation();
        conversation.setId(convId);
        conversation.setName("Old name");

        UpdateConversationDTO dto = new UpdateConversationDTO();
        dto.setName("New name");

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conversation));
        when(conversationRepository.save(conversation)).thenReturn(conversation);
        when(conversationMapper.toDto(conversation, currentUser, conversationMemberMapper))
                .thenReturn(new ConversationDTO());

        var response = service.updateConversation(convId, dto, currentUser);

        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(conversation.getName()).isEqualTo("New name");
    }

    @Test
    void deleteConversationById_marksDeleted() {
        UUID convId = UUID.randomUUID();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        Conversation conversation = new Conversation();
        conversation.setId(convId);

        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conversation));
        when(conversationMemberRepository.findByConversationIdAndUserId(convId, currentUser.getId()))
                .thenReturn(Optional.of(new ConversationMember()));

        var response = service.deleteConversationById(convId, currentUser);

        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        verify(conversationSoftDeleteService).markDeleted(currentUser.getId().toString(), convId.toString());
    }

    @Test
    void getAllConversation_filtersDeleted() {
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        currentUser.setUserName("testuser");

        Conversation conv1 = new Conversation();
        conv1.setId(UUID.randomUUID());
        Conversation conv2 = new Conversation();
        conv2.setId(UUID.randomUUID());

        // Repo trả về cả 2 conversation
        when(conversationRepository.findAllByMemberId(currentUser.getId()))
                .thenReturn(List.of(conv1, conv2));

        // Soft-delete trả về đúng
        when(conversationSoftDeleteService.isDeleted(currentUser.getId().toString(), conv1.getId().toString()))
                .thenReturn(false);
        when(conversationSoftDeleteService.isDeleted(currentUser.getId().toString(), conv2.getId().toString()))
                .thenReturn(true);

        // Mapper trả về DTO cho conversation không xoá
        when(conversationMapper.toDto(conv1, currentUser, conversationMemberMapper))
                .thenReturn(new ConversationDTO());

        var response = service.getAllConversation(currentUser);

        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1); // chỉ còn conv1
    }

}
