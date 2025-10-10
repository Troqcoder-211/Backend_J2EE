package j2ee.ourteam.models.message;

import j2ee.ourteam.models.enums.MessageTypeRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateMessageDTO {

  private String conversationId;

  private String sendeId;

  private String content;

  @Builder.Default
  private MessageTypeRequest messageType = MessageTypeRequest.TEXT;
}
