package j2ee.ourteam.models.message;

import lombok.Builder;

@Builder
public class DeleteMessageDTO {

  @Builder.Default
  private Boolean isDeleted = true;
}
