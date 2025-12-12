package j2ee.ourteam;

import j2ee.ourteam.controllers.WebSocketController;
import j2ee.ourteam.entities.*;
import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.enums.notification.NotificationTypeEnum;
import j2ee.ourteam.mapping.AttachmentMapper;
import j2ee.ourteam.mapping.MessageMapper;
import j2ee.ourteam.mapping.NotificationMapper;
import j2ee.ourteam.mapping.UserMapper;
import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.repositories.*;
import j2ee.ourteam.services.aws.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test") // dùng application-test.properties
public abstract class BaseTest {

    // ==================== Mock Repositories & Beans ====================
    @Mock protected UserRepository userRepository;
    @Mock protected MessageRepository messageRepository;
    @Mock protected MessageReactionRepository messageReactionRepository;
    @Mock protected MessageReadRepository messageReadRepository;
    @Mock protected ConversationRepository conversationRepository;
    @Mock protected ConversationMemberRepository conversationMemberRepository;
    @Mock protected AttachmentRepository attachmentRepository;
    @Mock protected DeviceRepository deviceRepository;
    @Mock protected NotificationRepository notificationRepository;

    @Mock protected WebSocketController webSocketController;
    @Mock protected MessageMapper messageMapper;
    @Mock protected NotificationMapper notificationMapper;
    @Mock protected S3Service s3Service;
    @Mock protected AttachmentMapper attachmentMapper;
    @Mock protected RefreshTokenRepository refreshTokenRepository;
    @Mock protected PresenceRepository presenceRepository;
    @Mock protected UserMapper userMapper;

    // ==================== Init ====================
    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== Utility Methods ====================
    protected UUID randomUUID() {
        return UUID.randomUUID();
    }

    protected LocalDateTime now() {
        return LocalDateTime.now();
    }

    // ==================== Entity Helpers ====================
    // User
    protected User mockUser() {
        return User.builder()
                .id(randomUUID())
                .userName("user_" + randomUUID().toString().substring(0, 5))
                .email("test@example.com")
                .password("password")
                .build();
    }

    // Message
    protected Message mockMessage(User sender, Conversation conversation) {
        return Message.builder()
                .id(randomUUID())
                .content("Hello world")
                .type(Message.MessageType.TEXT)
                .sender(sender)
                .conversation(conversation)
                .createdAt(now())
                .build();
    }

    // Conversation
    protected Conversation mockConversation(List<ConversationMember> members) {
        return Conversation.builder()
                .id(randomUUID())
                .name("Test Conversation")
                .members(members)
                .build();
    }

    // Device
    protected Device mockDevice(User user) {
        return Device.builder()
                .id(randomUUID())
                .user(user)
                .deviceType("ANDROID") // hoặc "IOS", tùy test case
                .pushToken("push_token_" + randomUUID())
                .createdAt(now())
                .lastSeenAt(now())
                .notifications(Collections.emptyList()) // mặc định chưa có notification
                .build();
    }

    // Notification
    protected Notification mockNotification(User user) {
        return Notification.builder()
                .id(randomUUID())
                .user(user)
                .type(NotificationTypeEnum.MESSAGE)
                .payload("Notification payload")
                .isRead(false)
                .isDelivered(false)
                .createdAt(now())
                .build();
    }

    // Attachment
    protected Attachment mockAttachment(User uploader, Conversation conversation) {
        return Attachment.builder()
                .id(randomUUID())
                .uploader(uploader)
                .conversation(conversation)
                .filename("file.txt")
                .mimeType("text/plain")
                .s3Bucket("test-bucket")
                .s3Key("test-key")
                .thumbnailS3Key("thumb-key")
                .sizeBytes(1024L)
                .checksum("checksum")
                .createdAt(now())
                .build();
    }

    // ==================== DTO Helpers ====================
    protected CreateMessageDTO mockCreateMessageDTO(UUID conversationId, UUID senderId) {
        return CreateMessageDTO.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content("Hello DTO")
                .messageType(MessageTypeEnum.TEXT)
                .attachmentIds(Collections.emptyList())
                .build();
    }

    protected CreateNotificationDTO mockCreateNotificationDTO(UUID userId) {
        return CreateNotificationDTO.builder()
                .userId(userId)
                .type(NotificationTypeEnum.MESSAGE)
                .payload("Test payload")
                .build();
    }

    // ==================== Default Mocking for Common Scenarios ====================
    protected void mockUserRepository(User user) {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    }

    protected void mockConversationRepository(Conversation conversation) {
        when(conversationRepository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
    }

    protected void mockMessageRepository(Message message) {
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));
    }

    protected void mockNotificationRepository(Notification notification) {
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
    }

    protected void mockDeviceRepository(User user, Device device) {
        when(deviceRepository.findByUserId(user.getId())).thenReturn(List.of(device));
    }
}
