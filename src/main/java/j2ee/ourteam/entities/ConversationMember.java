package j2ee.ourteam.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "conversation_members")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMember {

  @Builder.Default
  @EmbeddedId
  private ConversationMemberId id = new ConversationMemberId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("conversationId") // map field trong ConversationMember
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  // Cột phụ
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role = Role.MEMBER;

  @Builder.Default
  @Column(name = "joined_at", nullable = false)
  private LocalDateTime joinedAt = LocalDateTime.now();

  @Builder.Default
  @Column(name = "is_muted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private Boolean isMuted = false;

  @Column(name = "last_read_message_id")
  private UUID lastReadMessageId;

  @Column(name = "last_read_at")
  private LocalDateTime lastReadAt;

  public enum Role {
    MEMBER, OWNER, ADMIN
  }
}
