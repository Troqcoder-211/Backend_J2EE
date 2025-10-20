package j2ee.ourteam.models.auth;

import jakarta.validation.constraints.NotBlank;
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
public class ResetPasswordRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String otpCode;

    @NotBlank
    @Size(min = 8)
    private String newPassword;
}
