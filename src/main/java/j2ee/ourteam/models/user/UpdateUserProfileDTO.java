package j2ee.ourteam.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
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
public class UpdateUserProfileDTO {
    @Email
    private String email;
    @Size(max = 100, message = "Display name không được vượt quá 100 ký tự")
    private String displayName;
    private String avatarS3Key;
}
