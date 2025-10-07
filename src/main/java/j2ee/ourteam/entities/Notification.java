package j2ee.ourteam.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

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
  private String type;

  // JSON dữ liệu (Spring sẽ map thành chuỗi)
  @Lob
  @Column(columnDefinition = "jsonb")
  private String payload;

  @Builder.Default
  @Column(name = "is_delivered", nullable = false)
  private Boolean isDelivered = false;

  private Instant deliveredAt;

  @Builder.Default
  @Column(name = "is_read", nullable = false)
  private Boolean isRead = false;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt = LocalDate.now();
}
