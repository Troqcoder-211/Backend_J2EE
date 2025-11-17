package j2ee.ourteam.services.conversation;

import j2ee.ourteam.entities.*;
import j2ee.ourteam.mapping.ConversationMapper;
import j2ee.ourteam.models.conversation.*;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConversationServiceImplTest {

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConversationMemberRepository conversationMemberRepository;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private User adminUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminUser = User.builder().id(UUID.randomUUID()).userName("admin").build();
    }

    // ==================== CREATE CONVERSATION ====================
    @Test
    void createConversation_success() {
        CreateConversationDTO dto = new CreateConversationDTO();
        dto.setName("Group Chat");
        ConversationMemberDTO member = new ConversationMemberDTO();
        member.setUserId(UUID.randomUUID());
        dto.setMembers(List.of(member));

        Conversation conv = Conversation.builder().id(UUID.randomUUID()).build();
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(conversationMapper.toEntity(dto)).thenReturn(conv);
        when(conversationRepository.save(conv)).thenReturn(conv);
        when(conversationMemberRepository.findByIdConversationId(conv.getId())).thenReturn(List.of());

        var response = conversationService.createConversation(dto, adminUser);

        assertTrue(response.isSuccess());
        assertEquals("Tạo conversation thành công", response.getMessage());
        verify(conversationRepository).save(conv);
        verify(conversationMemberRepository, atLeastOnce()).save(any(ConversationMember.class));
    }

    @Test
    void createConversation_userNotFound() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.empty());

        var response = conversationService.createConversation(new CreateConversationDTO(), adminUser);
        assertFalse(response.isSuccess());
        assertEquals("User not found", response.getMessage());
    }

    // ==================== UPDATE CONVERSATION ====================
    @Test
    void updateConversation_success() {
        UpdateConversationDTO dto = new UpdateConversationDTO();
        dto.setName("New Name");

        Conversation conv = Conversation.builder().id(UUID.randomUUID()).createdBy(adminUser).build();
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(conversationRepository.findById(conv.getId())).thenReturn(Optional.of(conv));
        when(conversationRepository.save(conv)).thenReturn(conv);
        when(conversationMapper.toDto(conv)).thenReturn(new ConversationDTO());

        var response = conversationService.updateConversation(conv.getId(), dto, adminUser);
        assertTrue(response.isSuccess());
        assertEquals("Cập nhật conversation thành công", response.getMessage());
        verify(conversationRepository).save(conv);
    }

    @Test
    void updateConversation_notAdmin() {
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        UpdateConversationDTO dto = new UpdateConversationDTO();
        Conversation conv = Conversation.builder().id(UUID.randomUUID()).createdBy(otherUser).build();

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(conversationRepository.findById(conv.getId())).thenReturn(Optional.of(conv));

        var response = conversationService.updateConversation(conv.getId(), dto, adminUser);
        assertFalse(response.isSuccess());
        assertEquals("Only the group Admin can rename or change avatar", response.getMessage());
    }

    // ==================== DELETE CONVERSATION ====================
    @Test
    void deleteConversationById_success() {
        UUID convId = UUID.randomUUID();
        Conversation conv = Conversation.builder().id(convId).createdBy(adminUser).build();

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conv));

        var response = conversationService.deleteConversationById(convId, adminUser);
        assertTrue(response.isSuccess());
        assertEquals("Xóa conversation thành công", response.getMessage());
        verify(conversationRepository).deleteById(convId);
    }

    @Test
    void deleteConversationById_notAdmin() {
        UUID convId = UUID.randomUUID();
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        Conversation conv = Conversation.builder().id(convId).createdBy(otherUser).build();

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conv));

        var response = conversationService.deleteConversationById(convId, adminUser);
        assertFalse(response.isSuccess());
        assertEquals("Only the group Admin can delete it", response.getMessage());
    }

    // ==================== IS ARCHIVED ====================
    @Test
    void isArchived_success() {
        UUID convId = UUID.randomUUID();
        Conversation conv = Conversation.builder().id(convId).build();
        ArchivedConversationDTO dto = new ArchivedConversationDTO();

        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conv));
        when(conversationRepository.save(conv)).thenReturn(conv);

        var response = conversationService.isArchived(convId, dto, adminUser);
        assertTrue(response.isSuccess());
        assertEquals(true, response.getData());
        verify(conversationRepository).save(conv);
        verify(conversationMapper).updateArchivedFromDto(dto, conv);
    }

    // ==================== GET ALL CONVERSATION ====================
    @Test
    void getAllConversation_success() {
        Conversation conv1 = Conversation.builder().id(UUID.randomUUID()).build();
        Conversation conv2 = Conversation.builder().id(UUID.randomUUID()).build();
        when(conversationRepository.findAllByMemberId(adminUser.getId()))
                .thenReturn(List.of(conv1, conv2));
        when(conversationMapper.toDto(any())).thenReturn(new ConversationDTO());

        var response = conversationService.getAllConversation(adminUser);
        assertTrue(response.isSuccess());
        assertEquals(2, response.getData().size());
    }

    // ==================== FIND CONVERSATION BY ID ====================
    @Test
    void findConversationById_success() {
        UUID convId = UUID.randomUUID();
        Conversation conv = Conversation.builder().id(convId).build();
        when(conversationRepository.findById(convId)).thenReturn(Optional.of(conv));
        when(conversationMapper.toDto(conv)).thenReturn(new ConversationDTO());

        var response = conversationService.findConversationById(convId, adminUser);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
    }

    @Test
    void findConversationById_notFound() {
        UUID convId = UUID.randomUUID();
        when(conversationRepository.findById(convId)).thenReturn(Optional.empty());

        var response = conversationService.findConversationById(convId, adminUser);
        assertFalse(response.isSuccess());
        assertEquals("Conversation not found", response.getMessage());
    }
}
