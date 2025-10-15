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
public class CreateMessageReadDTO {
  @org.hibernate.validator.constraints.UUID
  private UUID messageId;

  @Builder.Default
  private LocalDate readAt = LocalDate.now();
}
