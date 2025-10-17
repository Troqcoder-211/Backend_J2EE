package j2ee.ourteam.models.conversation;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationDTO {
    private UUID id;
    private Conversation.ConversationType conversationType;
    private String name;
    private String avatarS3Key;
    private User createdBy;
    private LocalDate createdAt;
    private Boolean isArchived;
}