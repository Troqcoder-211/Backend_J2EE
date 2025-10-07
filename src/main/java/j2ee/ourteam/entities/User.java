package j2ee.ourteam.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "username", nullable = false, unique = true)
  private String userName;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "avatar_s3_key")
  private String avatarS3Key;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Builder.Default
  @Column(name = "is_disabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private Boolean isDisabled = false;

  // ========================
  // 🔗 RELATIONSHIPS
  // ========================
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Device> devices;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConversationMember> conversationMembers;

  @OneToMany(mappedBy = "created_by", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Conversation> createdConversations;

  @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> sentMessages;

  @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Attachment> uploadedAttachments;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageReaction> messageReactions;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageRead> messageReads;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notification> notifications;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Presence presence;

  @PrePersist
  protected void onCreate() {
    if (this.isDisabled == null)
      this.isDisabled = true;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

}
