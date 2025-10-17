package j2ee.ourteam.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class Device {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "device_type")
  private String deviceType;

  @Column(name = "push_token")
  private String pushToken;

  @Column(name = "last_seen_at")
  private LocalDateTime lastSeenAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // ========================
  // 🔗 RELATIONSHIPS
  // ========================
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notification> notifications;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.lastSeenAt = LocalDateTime.now();
  }
}
