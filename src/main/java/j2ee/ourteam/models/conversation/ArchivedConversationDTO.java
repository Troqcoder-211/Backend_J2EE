package j2ee.ourteam.models.conversation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArchivedConversationDTO {

    @org.hibernate.validator.constraints.UUID
    private UUID id;

    private Boolean isArchived;
}
