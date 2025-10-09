package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.Presence;

@Mapper(componentModel = "spring")
public interface PresenceMapper {
  Presence toDto(Presence p);

  Presence toEntity(Presence p);
}
