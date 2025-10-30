package j2ee.ourteam.models.conversation_member;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddConversationMemberDTO {
    @NotNull (message = "User id is not null.")
    private UUID userId;

    private String role = "Member";
}
