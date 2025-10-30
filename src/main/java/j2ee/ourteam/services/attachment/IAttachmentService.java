package j2ee.ourteam.services.attachment;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import j2ee.ourteam.models.attachment.AttachmentDTO;

public interface IAttachmentService {
  AttachmentDTO uploadFile(MultipartFile file, UUID userId, UUID conversationId);

  AttachmentDTO getMetaData(UUID id);

  void downloadFile(UUID id);

  void deleteAttachment(UUID id);
}
