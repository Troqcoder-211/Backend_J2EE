package j2ee.ourteam.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.models.notification.NotificationDTO;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

  @Mapping(target = "userId", expression = "java(notification.getUser() != null ? notification.getUser().getId() : null)")
  @Mapping(target = "deviceId", expression = "java(notification.getDevice() != null ? notification.getDevice().getId() : null)")
  @Mapping(target = "isDelivered", source = "isDelivered")
  NotificationDTO toDto(Notification notification);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "device", ignore = true)
  @Mapping(target = "isDelivered", ignore = true)
  @Mapping(target = "deliveredAt", ignore = true)
  @Mapping(target = "isRead", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Notification toEntity(CreateNotificationDTO notificationDTO);
}
