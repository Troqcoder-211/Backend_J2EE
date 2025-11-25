package j2ee.ourteam.services.attachment;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import j2ee.ourteam.models.attachment.AttachmentDTO;

public interface IAttachmentService {
  AttachmentDTO uploadFile(MultipartFile file, UUID userId, UUID conversationId);

  Page<AttachmentDTO> getAttachmentsByConversation(UUID conversationId, Pageable pageable);

  AttachmentDTO getMetaData(UUID id);

  void downloadFile(UUID id);

  void deleteAttachment(UUID id);
}
