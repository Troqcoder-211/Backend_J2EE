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
