package j2ee.ourteam.models.messagereaction;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class MessageReactionDTO {
  private UUID messageId;

  private UUID userId;

  private String emoji;

  private LocalDate createdAt;
}
