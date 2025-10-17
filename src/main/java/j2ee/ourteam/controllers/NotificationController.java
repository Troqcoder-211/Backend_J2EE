package j2ee.ourteam.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.models.notification.NotificationDTO;
import j2ee.ourteam.models.page.PageFilter;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.services.notification.INotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("notifications")
@AllArgsConstructor
public class NotificationController {
  @Autowired
  private final INotificationService notificationService;

  // /notifications POST
  @PostMapping
  public ResponseEntity<String> createNotification(@RequestBody @Valid CreateNotificationDTO notificationDTO) {
    notificationService.create(notificationDTO);
    return ResponseEntity.ok("Gửi thông báo thành công");
  }

  // /notifications GET
  @GetMapping("/{userId}")
  public ResponseEntity<PageResponse<NotificationDTO>> getNotification(@PathVariable UUID userId,
      @RequestBody @Valid PageFilter pageFilter) {
    Page<NotificationDTO> page = notificationService.getUserNotifications(userId, pageFilter);
    return ResponseEntity.ok(PageResponse.from(page));
  }

  // /notifications/{id}/read PATCH
  @PatchMapping("/{id}/read")
  public ResponseEntity<NotificationDTO> markAsRead(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.markAsRead(id));
  }

  // /notifications/{id}/delivered PATCH
  @PatchMapping("/{id}/delivered")
  public ResponseEntity<NotificationDTO> markAsDelivered(@PathVariable UUID id) {
    return ResponseEntity.ok(notificationService.markAsDelivered(id));
  }

  // /notifications/{id} DELETE
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable UUID id) {
    notificationService.deleteById(id);
    Map<String, Object> response = new HashMap<>();

    response.put("status", "success");
    response.put("message", "Notificataion is deleted");

    return ResponseEntity.ok(response);
  }
}
