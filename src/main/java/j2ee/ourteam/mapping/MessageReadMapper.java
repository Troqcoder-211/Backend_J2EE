package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.MessageRead;

@Mapper(componentModel = "spring")
public interface MessageReadMapper {
  MessageRead toDto(MessageRead m);

  MessageRead toEntity(MessageRead m);
}
