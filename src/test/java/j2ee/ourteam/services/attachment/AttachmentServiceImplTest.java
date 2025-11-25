package j2ee.ourteam.services.attachment;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.entities.Attachment;
import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.attachment.AttachmentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class AttachmentServiceImplTest extends BaseTest {

    private AttachmentServiceImpl attachmentService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        attachmentService = new AttachmentServiceImpl(
                attachmentRepository,
                conversationRepository,
                userRepository,
                s3Service,
                attachmentMapper
        );

        // Set bucketName bằng reflection vì private @Value
        try {
            java.lang.reflect.Field field = AttachmentServiceImpl.class.getDeclaredField("bucketName");
            field.setAccessible(true);
            field.set(attachmentService, bucketName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------- uploadFile --------------------
    @Test
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello".getBytes()
        );

        User user = mockUser(); // BaseTest helper
        Conversation conversation = mockConversation(Collections.emptyList()); // BaseTest helper
        Attachment attachment = mockAttachment(user, conversation); // BaseTest helper
        AttachmentDTO attachmentDTO = new AttachmentDTO(); // hoặc mockAttachmentDTO() nếu có

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(conversationRepository.findById(conversation.getId())).thenReturn(Optional.of(conversation));
        Mockito.when(s3Service.uploadFile(file)).thenReturn("test-key");
        Mockito.when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        Mockito.when(attachmentMapper.toDto(any(Attachment.class))).thenReturn(attachmentDTO);

        AttachmentDTO result = attachmentService.uploadFile(file, user.getId(), conversation.getId());

        assertThat(result).isEqualTo(attachmentDTO);

        Mockito.verify(s3Service).uploadFile(file);
        Mockito.verify(attachmentRepository).save(any(Attachment.class));
        Mockito.verify(attachmentMapper).toDto(any(Attachment.class));
    }


    @Test
    void uploadFile_userNotFound_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello".getBytes()
        );
        UUID userId = randomUUID();
        UUID convId = randomUUID();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.uploadFile(file, userId, convId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void uploadFile_conversationNotFound_shouldThrow() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello".getBytes()
        );
        UUID userId = randomUUID();
        UUID convId = randomUUID();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser()));
        Mockito.when(conversationRepository.findById(convId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.uploadFile(file, userId, convId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Conversation not found");
    }

    // -------------------- getMetaData --------------------
    @Test
    void getMetaData_success() {
        Attachment attachment = Attachment.builder().id(randomUUID()).build();
        AttachmentDTO attachmentDTO = new AttachmentDTO();

        Mockito.when(attachmentRepository.findById(attachment.getId())).thenReturn(Optional.of(attachment));
        Mockito.when(attachmentMapper.toDto(attachment)).thenReturn(attachmentDTO);

        AttachmentDTO result = attachmentService.getMetaData(attachment.getId());

        assertThat(result).isEqualTo(attachmentDTO);
    }

    @Test
    void getMetaData_notFound_shouldThrow() {
        UUID attachId = randomUUID();
        Mockito.when(attachmentRepository.findById(attachId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.getMetaData(attachId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Attachment not found");
    }

    // -------------------- downloadFile --------------------
    @Test
    void downloadFile_keyWithHttp_shouldProcessKey() {
        Attachment attachment = Attachment.builder()
                .id(randomUUID())
                .s3Key("https://example.com/path/key123")
                .build();

        Mockito.when(attachmentRepository.findById(attachment.getId())).thenReturn(Optional.of(attachment));

        attachmentService.downloadFile(attachment.getId());

        Mockito.verify(attachmentRepository).findById(attachment.getId());
    }

    @Test
    void downloadFile_notFound_shouldThrow() {
        UUID attachId = randomUUID();
        Mockito.when(attachmentRepository.findById(attachId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.downloadFile(attachId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Attachment not found");
    }

    // -------------------- deleteAttachment --------------------
    @Test
    void deleteAttachment_success() {
        Attachment attachment = Attachment.builder().id(randomUUID()).s3Key("key123").build();

        Mockito.when(attachmentRepository.findById(attachment.getId())).thenReturn(Optional.of(attachment));

        attachmentService.deleteAttachment(attachment.getId());

        Mockito.verify(s3Service).deleteFile("key123");
        Mockito.verify(attachmentRepository).delete(attachment);
    }

    @Test
    void deleteAttachment_notFound_shouldThrow() {
        UUID attachId = randomUUID();
        Mockito.when(attachmentRepository.findById(attachId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attachmentService.deleteAttachment(attachId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Attachment not found");
    }
}
