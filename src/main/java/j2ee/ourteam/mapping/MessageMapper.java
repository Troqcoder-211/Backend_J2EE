package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {
  Message toDto(Message m);

  Message toEntity(Message m);
}
