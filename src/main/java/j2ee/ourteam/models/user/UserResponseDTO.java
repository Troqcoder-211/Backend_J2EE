package j2ee.ourteam.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String userName;
    private String displayName;
    private String avatarS3Key;
    private Boolean isDisabled;
}
