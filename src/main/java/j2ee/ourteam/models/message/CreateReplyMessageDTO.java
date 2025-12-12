package j2ee.ourteam.models.message;

import java.util.List;
import java.util.UUID;

import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.validators.ValidUUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateReplyMessageDTO {

  @NotNull(message = "Conversation ID cannot be null")
  private UUID conversationId;

  @NotNull(message = "Sender ID cannot be null")
  private UUID senderId;

  @NotBlank(message = "Content cannot be empty")
  private String content;

  @NotNull(message = "ReplyTo message id cannot be null")
  private UUID replyToMessageId;

  @Size(max = 10, message = "You can attach up to 10 files per message")
  private List<@ValidUUID UUID> attachmentIds;

  @Builder.Default
  private MessageTypeEnum messageType = MessageTypeEnum.TEXT;
}