package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.Attachment;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
  Attachment toDto(Attachment a);

  Attachment toEntity(Attachment a);
}
