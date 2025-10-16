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
      @Mapping(target = "uploaderId", source = "uploader.id"),
      @Mapping(target = "conversationId", source = "conversation.id"),
  })
  AttachmentDTO toDto(Attachment attachment);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "conversation", ignore = true),
      @Mapping(target = "uploader", ignore = true),
      @Mapping(target = "", source = "", ignore = true),
      @Mapping(target = "", expression = "java(java.time.LocalDate.now())"),
  })
  Attachment toEntity(AttachmentDTO attachmentDTO);
}
