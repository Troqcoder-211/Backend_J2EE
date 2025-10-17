package j2ee.ourteam.models.notification;

import java.util.UUID;

import j2ee.ourteam.enums.notification.NotificationTypeEnum;
import jakarta.persistence.Column;
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
public class CreateNotificationDTO {
  @org.hibernate.validator.constraints.UUID
  private UUID userId;

  @org.hibernate.validator.constraints.UUID
  private UUID deviceId;

  @Enumerated(EnumType.STRING)
  private NotificationTypeEnum type;

  @Column(columnDefinition = "TEXT")
  private String payload;
}
