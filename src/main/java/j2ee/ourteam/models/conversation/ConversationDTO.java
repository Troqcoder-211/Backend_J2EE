package j2ee.ourteam.models.conversation;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.User;

import java.time.LocalDate;
import java.util.UUID;

public class ConversationDTO {
    private UUID id;
    private Conversation.ConversationType conversationType;
    private String name;
    private String avatarS3Key;
    private User createdBy;
    private LocalDate createdAt;
    private Boolean isArchived;
}
