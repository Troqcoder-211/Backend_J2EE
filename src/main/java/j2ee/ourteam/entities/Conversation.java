package j2ee.ourteam.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conversations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "conversation_type")
  private ConversationType conversationType;

  @Column(nullable = true)
  private String name;

  @Column(name = "avatar_s3_key")
  private String avatarS3Key;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private User createdBy;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "is_archived")
  private Boolean isArchived;

  // ========================
  // 🔗 RELATIONSHIPS
  // ========================
  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConversationMember> members;

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public enum ConversationType {
    DM,
    GROUP,
  }
}
