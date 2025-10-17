package j2ee.ourteam.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserResponseDTO {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String avatarS3Key;
    private boolean isDisabled;
}
