package j2ee.ourteam.models.messageread;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageReadDTO {
  private UUID messageId;
  private UUID userId;
  private String username;
  private LocalDate readAt;
}
