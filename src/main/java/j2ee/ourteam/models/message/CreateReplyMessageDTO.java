package j2ee.ourteam.models.message;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * DTO used by frontend to send a reply message.
 * Contains conversationId, senderId, content and the id of the message being
 * replied to.
 */
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

}
