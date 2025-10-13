package j2ee.ourteam.models.message;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UpdateMessageDTO {
  @NotBlank(message = "Content isn't empty")
  private String content;

  @Builder.Default
  private LocalDate editedAt = LocalDate.now();
}
