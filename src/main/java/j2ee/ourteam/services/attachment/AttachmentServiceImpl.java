package j2ee.ourteam.services.attachment;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import j2ee.ourteam.entities.Attachment;
import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.AttachmentMapper;
import j2ee.ourteam.models.attachment.AttachmentDTO;
import j2ee.ourteam.models.attachment.AttachmentDownloadDTO;
import j2ee.ourteam.repositories.AttachmentRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import j2ee.ourteam.services.aws.S3Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements IAttachmentService {

  private final AttachmentRepository attachmentRepository;
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final S3Service s3Service;

  private final AttachmentMapper attachmentMapper;

  @Value("${aws.s3.bucket}")
  private String bucketName;

  @Override
  public AttachmentDTO uploadFile(MultipartFile file, UUID uploaderId, UUID conversationId) {
    try {

      String key = s3Service.uploadFile(file);

      User uploader = userRepository.findById(uploaderId).orElseThrow(() -> new RuntimeException("User not found"));

      Conversation conversation = conversationRepository.findById(conversationId)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conversation not found"));

      Attachment attachment = Attachment.builder()
          .uploader(uploader)
          .conversation(conversation)
          .s3Bucket(bucketName)
          .s3Key(key)
          .filename(file.getOriginalFilename())
          .mimeType(file.getContentType())
          .sizeBytes(file.getSize())
          .build();

      attachmentRepository.save(attachment);

      return attachmentMapper.toDto(attachment);
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload File " + e.getMessage(), e);
    }
  }

  @Override
  public AttachmentDTO getMetaData(UUID id) {
    try {
      Attachment attachment = attachmentRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));

      return attachmentMapper.toDto(attachment);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get Meta Data" + e.getMessage(), e);
    }
  }

  @Override
  public AttachmentDownloadDTO downloadFile(UUID id) {
    try {
      Attachment attachment = attachmentRepository.findById(id)
          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));

      String key = attachment.getS3Key();

      Resource resource = s3Service.download(key);

      return AttachmentDownloadDTO.builder()
          .filename(attachment.getFilename())
          .mimeType(attachment.getMimeType())
          .resource(resource)
          .build();

    } catch (Exception e) {
      throw new RuntimeException("Failed to download file", e);
    }
  }

  @Override
  public void deleteAttachment(UUID id) {
    Attachment attachment = attachmentRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
    try {
      s3Service.deleteFile(attachment.getS3Key());
      attachmentRepository.delete(attachment);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete attachment", e);
    }
  }

}
