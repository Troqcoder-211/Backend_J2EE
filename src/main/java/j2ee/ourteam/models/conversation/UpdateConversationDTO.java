package j2ee.ourteam.models.conversation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateConversationDTO {
    @org.hibernate.validator.constraints.UUID
    private UUID id;

    @NotBlank(message = "Conversation name isn't empty.")
    private String name;

    private String avatarS3Key;

}
