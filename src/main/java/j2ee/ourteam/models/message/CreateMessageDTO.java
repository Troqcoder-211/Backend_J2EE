package j2ee.ourteam.models.message;

import org.hibernate.validator.constraints.UUID;

import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.validators.ValidUUID;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateMessageDTO {

  @UUID
  private UUID conversationId;

  @UUID
  private UUID senderId;

  @NotBlank(message = "Content isn't empty")
  private String content;

  @ValidUUID
  private UUID replyTo;

  @NotBlank(message = "Message Type isn't empty")

  @Builder.Default
  private MessageTypeEnum messageType = MessageTypeEnum.TEXT;

}
