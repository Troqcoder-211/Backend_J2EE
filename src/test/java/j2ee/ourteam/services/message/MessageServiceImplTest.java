package j2ee.ourteam.services.message;

import j2ee.ourteam.controllers.WebSocketController;
import j2ee.ourteam.entities.*;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceImplTest {

    private MessageRepository messageRepository;
    private MessageReactionRepository messageReactionRepository;
    private MessageReadRepository messageReadRepository;
    private ConversationRepository conversationRepository;
    private ConversationMemberRepository conversationMemberRepository;
    private AttachmentRepository attachmentRepository;
    private UserRepository userRepository;
    private MessageMapper messageMapper;
    private WebSocketController webSocketController;

    private MessageServiceImpl service;

    @BeforeEach
    void setUp() {
        messageRepository = mock(MessageRepository.class);
        messageReactionRepository = mock(MessageReactionRepository.class);
        messageReadRepository = mock(MessageReadRepository.class);
        conversationRepository = mock(ConversationRepository.class);
        conversationMemberRepository = mock(ConversationMemberRepository.class);
        attachmentRepository = mock(AttachmentRepository.class);
        userRepository = mock(UserRepository.class);
        messageMapper = mock(MessageMapper.class);
        webSocketController = mock(WebSocketController.class);

        service = new MessageServiceImpl(
                messageRepository,
                messageReactionRepository,
                messageReadRepository,
                conversationRepository,
                conversationMemberRepository,
                attachmentRepository,
                messageMapper,
                userRepository
        );
        service.webSocketController = webSocketController;
    }

    @Test
    void createMessage_shouldSaveAndPushMessage() {
        UUID conversationId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        CreateMessageDTO dto = new CreateMessageDTO();
        dto.setConversationId(conversationId);
        dto.setSenderId(senderId);
        dto.setMessageType(MessageTypeEnum.TEXT);
        dto.setContent("Hello world");

        Conversation conversation = new Conversation();
        conversation.setId(conversationId);
        User sender = new User();
        sender.setId(senderId);

        Message savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        MessageDTO savedDTO = new MessageDTO();
        savedDTO.setId(savedMessage.getId());

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(messageMapper.toEntity(dto)).thenReturn(new Message());
        when(messageRepository.save(any())).thenReturn(savedMessage);
        when(messageMapper.toDto(savedMessage)).thenReturn(savedDTO);

        MessageDTO result = service.create(dto);

        assertEquals(savedDTO, result);
        verify(messageRepository).save(any());
        verify(webSocketController).pushMessage(savedDTO);
    }

    @Test
    void createMessage_shouldThrowIfContentEmptyForText() {
        CreateMessageDTO dto = new CreateMessageDTO();
        dto.setMessageType(MessageTypeEnum.TEXT);
        dto.setContent("  ");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(dto));
        assertTrue(ex.getMessage().contains("Content cannot be empty"));
    }

    @Test
    void addReaction_shouldSaveReaction() {
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Message message = new Message();
        message.setId(messageId);
        User user = new User();
        user.setId(userId);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageReactionRepository.existsById(any())).thenReturn(false);
        when(messageReactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = new j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO();
        dto.setUserId(userId);
        dto.setEmoji("👍");

        assertDoesNotThrow(() -> service.addReaction(messageId, dto));
        verify(messageReactionRepository).save(any());
    }

    @Test
    void addReaction_shouldFailIfReactionExists() {
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Message message = new Message();
        message.setId(messageId);
        User user = new User();
        user.setId(userId);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageReactionRepository.existsById(any())).thenReturn(true);

        var dto = new j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO();
        dto.setUserId(userId);
        dto.setEmoji("👍");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.addReaction(messageId, dto));
        assertTrue(ex.getMessage().contains("Reaction already exists"));
    }

    @Test
    void findAll_shouldReturnList() {
        Message message = new Message();
        UUID id = UUID.randomUUID();
        message.setId(id);
        MessageDTO dto = new MessageDTO();
        dto.setId(id);

        when(messageRepository.findAll()).thenReturn(List.of(message));
        when(messageMapper.toDto(message)).thenReturn(dto);

        var result = service.findAll();
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    // Bạn có thể thêm test tương tự cho: reply, softDelete, deleteById, markConversationAsRead...
}
