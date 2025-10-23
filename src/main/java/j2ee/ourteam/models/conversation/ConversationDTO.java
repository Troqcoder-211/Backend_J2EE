package j2ee.ourteam.models.conversation;

import j2ee.ourteam.entities.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
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
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isArchived;
}