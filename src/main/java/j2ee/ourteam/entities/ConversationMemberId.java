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
