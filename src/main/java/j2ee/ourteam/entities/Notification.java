package j2ee.ourteam.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import j2ee.ourteam.enums.notification.NotificationTypeEnum;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "device_id")
  private Device device;

  // Loại thông báo (ví dụ: MESSAGE, REACTION, SYSTEM, INVITE, v.v.)
  @Enumerated(EnumType.STRING)
  private NotificationTypeEnum type;

  // JSON dữ liệu (Spring sẽ map thành chuỗi)
  @Column(columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private Object payload;

  @Builder.Default
  @Column(name = "is_delivered", nullable = false)
  private Boolean isDelivered = false;

  @Builder.Default
  private LocalDateTime deliveredAt = LocalDateTime.now();

  @Builder.Default
  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

}
