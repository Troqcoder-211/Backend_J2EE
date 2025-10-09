package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;

import j2ee.ourteam.entities.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
  Notification toDto(Notification n);

  Notification toEntity(Notification n);
}
