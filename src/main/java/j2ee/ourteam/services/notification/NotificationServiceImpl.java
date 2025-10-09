package j2ee.ourteam.services.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.repositories.NotificationRepository;

@Service
public class NotificationServiceImpl implements INotificationService {

  private final NotificationRepository notificationRepository;

  public NotificationServiceImpl(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Override
  public List<Notification> findAll() {
    return notificationRepository.findAll();
  }

  @Override
  public Optional<Notification> findById(UUID id) {
    return notificationRepository.findById(id);
  }

  @Override
  public Notification save(Notification entity) {
    return notificationRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    notificationRepository.deleteById(id);
  }

}
