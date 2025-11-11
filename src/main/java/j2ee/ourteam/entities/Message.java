package j2ee.ourteam.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(nullable = false)
  private MessageType type = MessageType.TEXT;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reply_to_message_id")
  private Message replyTo;

  @Builder.Default
  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "edited_at")
  private LocalDateTime editedAt;

  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  // ========================
  // 🔗 RELATIONSHIPS
  // ========================
  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageReaction> reactions;

  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageRead> reads;

  @OneToMany(mappedBy = "replyTo", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> replies;

  // Liên kết attachments
  @Builder.Default
  @ManyToMany
  @JoinTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
  private Set<Attachment> attachments = new HashSet<>();

  public enum MessageType {
    TEXT, IMAGE, VIDEO, FILE, SYSTEM, REPLY
  }
}
