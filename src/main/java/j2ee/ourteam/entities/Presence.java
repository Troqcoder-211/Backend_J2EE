package j2ee.ourteam.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "presences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Presence {

  @Id
  @Column(name = "user_id")
  @EqualsAndHashCode.Include
  private UUID userId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id")
  private User user; // Exclude from equals/hashCode (implicitly, since not @Include)

  @Builder.Default
  @Column(name = "is_online", nullable = false)
  private Boolean isOnline = false;

  private LocalDateTime lastSeenAt;

  @Builder.Default
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();
}