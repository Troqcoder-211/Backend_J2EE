package j2ee.ourteam.models.message;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
  private UUID id;
  private String content;
  private String type;
  private LocalDate createdAt;
  private LocalDate editedAt;
  private Boolean isDeleted;
  private UUID senderId;
  private UUID conversationId;
  private UUID replyTo;

  private Set<UUID> attachmentIds;
}
