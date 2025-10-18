package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import j2ee.ourteam.entities.Attachment;
import j2ee.ourteam.models.attachment.AttachmentDTO;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

  @Mappings({
      @Mapping(target = "id", source = "id"),
      // Dùng expression để tránh null pointer / không tìm thấy property
      @Mapping(target = "uploaderId", expression = "java(attachment.getUploader() != null ? attachment.getUploader().getId() : null)"),
      @Mapping(target = "conversationId", expression = "java(attachment.getConversation() != null ? attachment.getConversation().getId() : null)")
  })
  AttachmentDTO toDto(Attachment attachment);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "conversation", ignore = true),
      @Mapping(target = "uploader", ignore = true),
      @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
      @Mapping(target = "messages", ignore = true)
  })
  Attachment toEntity(AttachmentDTO attachmentDTO);
}
