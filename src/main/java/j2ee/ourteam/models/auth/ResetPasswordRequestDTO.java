package j2ee.ourteam.models.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,128}$",
            message = "Password phải có ít nhất 8 ký tự, gồm chữ và số"
    )
    private String newPassword;
}
