package j2ee.ourteam.models.conversation;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;
import j2ee.ourteam.entities.Conversation;


@Data
@AllArgsConstructor
@Builder
public class CreateConversationDTO {

    @UUID
    private UUID conversationId;

    @NotBlank(message = "Conversation name isn't empty.")
    private String name;

    @NotBlank(message = "Conversation type isn't empty.")
    private Conversation.ConversationType conversationType;


    private String avatarS3Key;

    private User createdBy;

    @Builder.Default
    private Message.MessageType messageType = Message.MessageType.TEXT;
}
