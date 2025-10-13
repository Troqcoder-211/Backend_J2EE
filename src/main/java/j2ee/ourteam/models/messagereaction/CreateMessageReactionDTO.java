package j2ee.ourteam.models.messagereaction;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder
public class CreateMessageReactionDTO {
  @org.hibernate.validator.constraints.UUID
  private UUID messageId;

  @org.hibernate.validator.constraints.UUID
  private UUID userId;

  @NotBlank(message = "Emoji isn't empty")
  private String emoji;

  @Builder.Default
  private LocalDate createdAt = LocalDate.now();
}
