package j2ee.ourteam.models.messagereaction;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public class CreateMessageReactionDTO {
  @NotNull
  private UUID userId;

  @NotBlank(message = "Emoji isn't empty")
  private String emoji;

  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
