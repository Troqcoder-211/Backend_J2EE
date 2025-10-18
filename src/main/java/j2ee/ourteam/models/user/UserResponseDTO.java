package j2ee.ourteam.models.user;

import java.util.UUID;

import j2ee.ourteam.entities.User;
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
    private UUID id;
    private String userName;
    private String email;
    private String displayName;
    private String avatarS3Key;
    private String role;

    public UserResponseDTO(User user){
        this.id = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.avatarS3Key = user.getAvatarS3Key();
        this.role = user.getRole();
    }
}
