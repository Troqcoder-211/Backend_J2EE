// File: j2ee.ourteam.mapping.AttachmentMapper.java (TỐI ƯU HÓA)

package j2ee.ourteam.mapping;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import j2ee.ourteam.entities.Attachment;
import j2ee.ourteam.models.attachment.AttachmentDTO;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

  // 1. Ánh xạ Entity -> DTO (Từng phần tử)
  @Mappings({
      @Mapping(target = "id", source = "id"),
      @Mapping(target = "uploaderId", expression = "java(attachment.getUploader() != null ? attachment.getUploader().getId() : null)"),
      @Mapping(target = "conversationId", expression = "java(attachment.getConversation() != null ? attachment.getConversation().getId() : null)")
  })
  AttachmentDTO toDto(Attachment attachment);

  // 2. Ánh xạ DTO -> Entity (Từng phần tử)
  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "conversation", ignore = true),
      @Mapping(target = "uploader", ignore = true),
      @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
      @Mapping(target = "messages", ignore = true)
  })
  Attachment toEntity(AttachmentDTO attachmentDTO);

  Set<AttachmentDTO> toDtoSet(Set<Attachment> attachments);

  // Set<AttachmentDTO> -> Set<Attachment>
  Set<Attachment> toEntitySet(Set<AttachmentDTO> attachmentDTOs);
}