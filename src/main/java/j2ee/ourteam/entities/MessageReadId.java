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
