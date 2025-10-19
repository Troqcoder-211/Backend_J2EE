=== Attachment.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploader_id")
  private User uploader;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id")
  private Conversation conversation;

  @Column(name = "s3_bucket", nullable = false)
  private String s3Bucket;

  @Column(name = "s3_key", nullable = false)
  private String s3Key;

  @Column(name = "thumbnail_s3_key")
  private String thumbnailS3Key;

  @Column(name = "filename")
  private String filename;

  @Column(name = "mime_type")
  private String mimeType;

  @Column(name = "size_bytes")
  private Long sizeBytes;

  @Column(name = "checksum")
  private String checksum;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt = LocalDate.now();

  @Builder.Default
  @ManyToMany(mappedBy = "attachments")
  private Set<Message> messages = new HashSet<>();

}


=== Conversation.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;
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

@Entity
@Table(name = "conversations")
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
  private LocalDate createdAt;

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
    this.createdAt = LocalDate.now();
  }

  public enum ConversationType {
    DM,
    GROUP,
  }
}


=== ConversationMember.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;
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
  private LocalDate joinedAt = LocalDate.now();

  @Builder.Default
  @Column(name = "is_muted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
  private Boolean isMuted = false;

  @Column(name = "last_read_message_id")
  private UUID lastReadMessageId;

  @Column(name = "last_read_at")
  private LocalDate lastReadAt;

  public enum Role {
    MEMBER, OWNER
  }
}


=== ConversationMemberId.java ===
package j2ee.ourteam.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConversationMemberId implements Serializable {
  private UUID conversationId;
  private UUID userId;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ConversationMemberId))
      return false;
    ConversationMemberId that = (ConversationMemberId) o;
    return Objects.equals(conversationId, that.conversationId)
        && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(conversationId, userId);
  }

}


=== Device.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;
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
  private LocalDate lastSeenAt;

  @Column(name = "created_at")
  private LocalDate createdAt;

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
    this.createdAt = LocalDate.now();
    this.lastSeenAt = LocalDate.now();
  }
}


=== Message.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;
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
  private LocalDate createdAt = LocalDate.now();

  @Column(name = "edited_at")
  private LocalDate editedAt;

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

  // Liên kết attachments
  @Builder.Default
  @ManyToMany
  @JoinTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"), inverseJoinColumns = @JoinColumn(name = "attachment_id"))
  private Set<Attachment> attachments = new HashSet<>();

  public enum MessageType {
    TEXT, IMAGE, VIDEO, FILE, SYSTEM, REPLY
  }
}


=== MessageReaction.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_reactions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReaction {

  @Builder.Default
  @EmbeddedId
  private MessageReactionId id = new MessageReactionId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("messageId") // map tới field trong MessageReactionId
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(insertable = false, updatable = false)
  private String emoji;


  @Builder.Default
  @Column(name = "reacted_At", nullable = false)
  private LocalDate reactedAt = LocalDate.now();
}


=== MessageReactionId.java ===
package j2ee.ourteam.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageReactionId implements Serializable {
  private UUID messageId;
  private UUID userId;
  private String emoji;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof MessageReactionId))
      return false;
    MessageReactionId that = (MessageReactionId) o;
    return Objects.equals(messageId, that.messageId)
        && Objects.equals(userId, that.userId)
        && Objects.equals(emoji, that.emoji);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageId, userId, emoji);
  }
}


=== MessageRead.java ===
package j2ee.ourteam.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_reads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRead {
  @Builder.Default
  @EmbeddedId
  private MessageReadId id = new MessageReadId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("messageId")
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Builder.Default
  @Column(name = "read_at", nullable = false)
  private LocalDate readAt = LocalDate.now();
}


=== MessageReadId.java ===
package j2ee.ourteam.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadId implements Serializable {
  private UUID messageId;
  private UUID userId;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof MessageReadId))
      return false;
    MessageReadId that = (MessageReadId) o;
    return Objects.equals(messageId, that.messageId)
        && Objects.equals(userId, that.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageId, userId);
  }

}


=== Notification.java ===
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


=== Presence.java ===
package j2ee.ourteam.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "presences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Builder.Default
  @Column(name = "is_online", nullable = false)
  private Boolean isOnline = false;

  private Instant lastSeenAt;

  @Builder.Default
  @Column(name = "updated_at", nullable = false)
  private LocalDate updatedAt = LocalDate.now();
}


=== User.java ===
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
    
@Column(nullable = false)
private String password;

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
