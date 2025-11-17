package j2ee.ourteam.services.notification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import j2ee.ourteam.controllers.WebSocketController;
import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.NotificationMapper;
import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.models.notification.NotificationDTO;
import j2ee.ourteam.models.page.PageFilter;
import j2ee.ourteam.repositories.DeviceRepository;
import j2ee.ourteam.repositories.NotificationRepository;
import j2ee.ourteam.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements INotificationService {
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final DeviceRepository deviceRepository;

  private final NotificationMapper notificationMapper;

  @Autowired
  WebSocketController webSocketController;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository, DeviceRepository deviceRepository, NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
    }

    @Override
  @Transactional
  public NotificationDTO create(Object dto) {
    if (!(dto instanceof CreateNotificationDTO createDto)) {
      throw new IllegalArgumentException("Invalid DTO type for create");
    }

    User user = userRepository.findById(createDto.getUserId())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    List<Device> devices = deviceRepository.findByUserId(user.getId());

    try {
      List<Notification> notifications = devices.stream()
          .<Notification>map(device -> notificationMapper.toEntity(createDto)).toList();

      // Ghi 1 lần
      notificationRepository.saveAll(notifications);

      webSocketController.pushNotification(notificationMapper.toDto(notifications.get(0)));

      return notificationMapper.toDto((notifications.get(0)));
    } catch (Exception e) {
      throw new RuntimeException("Failed to create notification: " + e.getMessage(), e);
    }
  }

  @Override
  public Page<NotificationDTO> getUserNotifications(UUID userId, PageFilter pageFilter) {
    try {
      Pageable pageable = PageRequest.of(
          pageFilter.getPage() - 1,
          pageFilter.getLimit(),
          Sort.Direction.fromString(pageFilter.getSortOrder()),
          pageFilter.getSortBy());
      return notificationRepository.findAll(pageable).map(notificationMapper::toDto);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get User Notification" + e.getMessage(), e);
    }
  }

  @Override
  public NotificationDTO markAsRead(UUID id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

    try {
      notification.setIsRead(true);

      notificationRepository.save(notification);

      return notificationMapper.toDto(notification);
    } catch (Exception e) {
      throw new RuntimeException("Failed to mark As Read" + e.getMessage(), e);
    }
  }

  @Override
  public NotificationDTO markAsDelivered(UUID id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

    try {
      notification.setIsDelivered(true);
      notification.setDeliveredAt(LocalDateTime.now());

      notificationRepository.save(notification);

      return notificationMapper.toDto(notification);
    } catch (Exception e) {
      throw new RuntimeException("Failed to mark As Delivered" + e.getMessage(), e);
    }
  }

  @Override
  public void deleteById(UUID id) {
    Notification notification = notificationRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

    try {
      notificationRepository.delete(notification);
    } catch (Exception e) {
      throw new RuntimeException("Failed to delete Notification" + e.getMessage(), e);
    }
  }

  // Unavailable
  @Override
  public List<NotificationDTO> findAll() {
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

  // Unavailable
  @Override
  public Optional<NotificationDTO> findById(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  // Unavailable
  @Override
  public NotificationDTO update(UUID id, Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

}
