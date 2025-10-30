package j2ee.ourteam.models.notification;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

import j2ee.ourteam.enums.notification.NotificationTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO implements Serializable {
  private UUID id;
  private UUID userId;
  private UUID deviceId;
  @Enumerated(EnumType.STRING)
  private NotificationTypeEnum type;
  private String payload;
  private boolean isDelivered;
  private LocalDate deliveredAt;
  private boolean isRead;
  private LocalDate createdAt;
}
