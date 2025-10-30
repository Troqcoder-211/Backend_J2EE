package j2ee.ourteam.models.conversation_member;

import j2ee.ourteam.entities.ConversationMember.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleDTO {
    @NotNull(message = "Role is not null.")
    private Role role;
}
