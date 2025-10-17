package j2ee.ourteam.models.conversation;

import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import j2ee.ourteam.entities.Conversation;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConversationDTO {
    @org.hibernate.validator.constraints.UUID
    private UUID id;

    @NotBlank(message = "Conversation name is not empty.")
    private String name;

    @NotNull(message = "Conversation type is not empty.")
    private Conversation.ConversationType conversationType;


    private String avatarS3Key;


//    private User createdBy;
//
//    @Builder.Default
//    private Message.MessageType messageType = Message.MessageType.TEXT;
}
