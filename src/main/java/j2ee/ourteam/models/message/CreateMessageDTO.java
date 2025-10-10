package j2ee.ourteam.models.message;

import org.hibernate.validator.constraints.UUID;

import j2ee.ourteam.models.enums.Request.MessageTypeRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateMessageDTO {

  @UUID
  private String conversationId;

  @UUID
  private String sendeId;

  @NotBlank(message = "Content isn't empty")
  private String content;

  @UUID
  private String replyTo;

  @NotBlank(message = "Message Type isn't empty")
  @Builder.Default
  private MessageTypeRequest messageType = MessageTypeRequest.TEXT;

}
