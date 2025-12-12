package j2ee.ourteam.services.notification;

import java.util.UUID;

import org.springframework.data.domain.Page;

import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.notification.NotificationDTO;
import j2ee.ourteam.models.page.PageFilter;

public interface INotificationService extends GenericCrudService<Notification, Object, NotificationDTO, UUID> {
  Page<NotificationDTO> getUserNotifications(UUID userId, PageFilter pageFilter);

  NotificationDTO markAsRead(UUID id);

  NotificationDTO markAsDelivered(UUID id);
}
