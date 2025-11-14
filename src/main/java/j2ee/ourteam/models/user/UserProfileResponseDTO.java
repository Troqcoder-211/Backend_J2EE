package j2ee.ourteam.models.user;

import java.util.UUID;

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
public class UserProfileResponseDTO {
    private UUID id;
    private String userName;
    private String email;
    private String displayName;
    private String avatarS3Key;
    private Boolean isDisable;
}
