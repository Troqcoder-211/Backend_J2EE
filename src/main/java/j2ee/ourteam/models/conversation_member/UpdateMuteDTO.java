package j2ee.ourteam.models.conversation_member;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateMuteDTO {
    @NotNull(message = "isMuted is not null.")
    private Boolean isMuted;
}
