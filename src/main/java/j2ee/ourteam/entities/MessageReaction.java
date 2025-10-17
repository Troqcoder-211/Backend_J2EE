package j2ee.ourteam.entities;

import java.time.LocalDateTime;

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

  @Builder.Default
  @Column(name = "reacted_At", nullable = false)
  private LocalDateTime reactedAt = LocalDateTime.now();
}
