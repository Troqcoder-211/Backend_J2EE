package j2ee.ourteam.services.message;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.controllers.WebSocketController;
import j2ee.ourteam.entities.*;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.mapping.AttachmentMapper;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.models.message.*;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MessageServiceImplTest extends BaseTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private WebSocketController webSocketController;

    private MessageServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MessageServiceImpl(
                messageRepository,
                messageReactionRepository,
                messageReadRepository,
                conversationRepository,
                conversationMemberRepository,
                attachmentRepository,
                userRepository,
                messageMapper,
                attachmentMapper, // <-- thêm tham số mới
                webSocketController
        );
    }

    @Test
    void create_shouldSaveTextMessage() {
        User sender = mockUser();
        Conversation conversation = mockConversation(List.of());
        CreateMessageDTO dto = mockCreateMessageDTO(conversation.getId(), sender.getId());

        Message message = mockMessage(sender, conversation);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setContent(dto.getContent());

        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(conversationRepository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
        when(messageMapper.toEntity(dto)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        MessageDTO result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo(dto.getContent());
        verify(webSocketController).pushMessage(messageDTO);
    }

    @Test
    void create_shouldThrowIfContentEmpty() {
        CreateMessageDTO dto = mockCreateMessageDTO(randomUUID(), randomUUID());
        dto.setContent(" ");
        dto.setMessageType(MessageTypeEnum.TEXT);

        assertThrows(Exception.class, () -> service.create(dto));
    }

    @Test
    void reply_shouldSaveReplyMessage() {
        User sender = mockUser();
        Conversation conv = mockConversation(List.of());
        Message original = mockMessage(sender, conv);

        CreateReplyMessageDTO dto = new CreateReplyMessageDTO(
                conv.getId(),
                sender.getId(),
                "Reply",
                original.getId(),
                List.of(),              // thêm danh sách người nhận rỗng
                MessageTypeEnum.TEXT    // thêm type
        );

        Message reply = mockMessage(sender, conv);
        MessageDTO replyDTO = new MessageDTO();
        replyDTO.setId(reply.getId());

        when(conversationRepository.findById(conv.getId())).thenReturn(Optional.of(conv));
        when(userRepository.findById(sender.getId())).thenReturn(Optional.of(sender));
        when(messageRepository.findById(original.getId())).thenReturn(Optional.of(original));
        when(messageRepository.save(any(Message.class))).thenReturn(reply);
        when(messageMapper.toDto(reply)).thenReturn(replyDTO);

        MessageDTO result = service.reply(dto);

        assertThat(result.getId()).isEqualTo(reply.getId());
        verify(messageReadRepository).insertMessageRead(eq(reply.getId()), eq(sender.getId()), any(LocalDateTime.class));
        verify(webSocketController).pushMessage(replyDTO);
    }

    @Test
    void update_shouldUpdateMessage() {
        Message message = mockMessage(mockUser(), mockConversation(List.of()));
        UpdateMessageDTO dto = new UpdateMessageDTO("Updated content", LocalDateTime.now());
        MessageDTO dtoResult = new MessageDTO();
        dtoResult.setId(message.getId());
        dtoResult.setContent(dto.getContent());

        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        when(messageMapper.toDto(message)).thenReturn(dtoResult);

        MessageDTO result = service.update(message.getId(), dto);

        assertThat(result.getContent()).isEqualTo(dto.getContent());
        verify(messageRepository).save(message);
    }

    @Test
    void softDelete_shouldMarkMessageDeleted() {
        Message message = mockMessage(mockUser(), mockConversation(List.of()));
        message.setIsDeleted(false);

        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());

        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        when(messageMapper.toDto(message)).thenReturn(dto);

        MessageDTO result = service.softDelete(message.getId());

        assertThat(result).isNotNull();
        assertThat(message.getIsDeleted()).isTrue();
        verify(messageRepository).save(message);
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        Message message = mockMessage(mockUser(), mockConversation(List.of()));
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));

        service.deleteById(message.getId());

        verify(messageRepository).delete(message);
    }

    @Test
    void findById_shouldReturnMessageDTO() {
        Message message = mockMessage(mockUser(), mockConversation(List.of()));
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());

        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        when(messageMapper.toDto(message)).thenReturn(dto);

        Optional<MessageDTO> result = service.findById(message.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(message.getId());
    }

    @Test
    void findAll_shouldReturnList() {
        Message message = mockMessage(mockUser(), mockConversation(List.of()));
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());

        when(messageRepository.findAll()).thenReturn(List.of(message));
        when(messageMapper.toDto(message)).thenReturn(dto);

        List<MessageDTO> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(message.getId());
    }

    @Test
    void addReaction_shouldSaveReaction() {
        Message message = mockMessage(mockUser(), mockConversation(List.of()));
        User user = mockUser();
        CreateMessageReactionDTO dto = new CreateMessageReactionDTO();
        dto.setUserId(user.getId());
        dto.setEmoji("👍");

        MessageReactionId key = new MessageReactionId(message.getId(), user.getId(), "👍");

        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(messageReactionRepository.existsById(key)).thenReturn(false);

        service.addReaction(message.getId(), dto);

        verify(messageReactionRepository).save(any(MessageReaction.class));
    }

    @Test
    void deleteReaction_shouldDeleteReaction() {
        UUID messageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String emoji = "👍";
        MessageReactionId key = new MessageReactionId(messageId, userId, emoji);

        when(messageReactionRepository.existsById(key)).thenReturn(true);

        service.deleteReaction(messageId, userId, emoji);

        verify(messageReactionRepository).deleteById(key);
    }
}
