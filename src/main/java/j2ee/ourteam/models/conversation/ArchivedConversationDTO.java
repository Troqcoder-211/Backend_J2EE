package j2ee.ourteam.models.conversation;

import org.hibernate.validator.constraints.UUID;

public class ArchivedConversationDTO {

    @UUID
    private UUID id;

    private Boolean isArchived;
}
