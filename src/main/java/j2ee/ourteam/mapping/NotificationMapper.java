package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.models.notification.NotificationDTO;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mappings({
      @Mapping(target = "id", source = "id"),
      @Mapping(target = "userId", source = "user.id"),
      @Mapping(target = "deviceId", source = "device.id"),
      @Mapping(target = "type", source = "type"),
      @Mapping(target = "payload", source = "payload"),
      @Mapping(target = "isDelivered", source = "payload"),
  })
  NotificationDTO toDto(Notification notification);

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "user", ignore = true),
      @Mapping(target = "device", ignore = true),
      @Mapping(target = "type", source = "type"),
      @Mapping(target = "payload", source = "payload"),
      @Mapping(target = "isDelivered", ignore = true),
      @Mapping(target = "deliveredAt", ignore = true),
      @Mapping(target = "isRead", ignore = true),
      @Mapping(target = "createdAt", ignore = true),
  })
  Notification toEntity(CreateNotificationDTO notificationDTO);
}
