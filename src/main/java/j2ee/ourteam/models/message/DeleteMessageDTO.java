package j2ee.ourteam.models.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageDTO {
  @Builder.Default
  private Boolean isDeleted = true;
}
