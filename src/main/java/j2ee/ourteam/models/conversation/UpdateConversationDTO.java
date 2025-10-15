package j2ee.ourteam.models.conversation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
@AllArgsConstructor
@Builder
public class UpdateConversationDTO {
    @UUID
    private UUID id;

    @NotBlank(message = "Conversation name isn't empty.")
    private String name;

    private String avatarS3Key;

}
