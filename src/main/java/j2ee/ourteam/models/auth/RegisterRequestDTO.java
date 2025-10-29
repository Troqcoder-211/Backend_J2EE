package j2ee.ourteam.models.auth;

import jakarta.validation.constraints.Email;
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
public class RegisterRequestDTO {
    @NotBlank(message = "Username không được để trống")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9._-]{3,99}$",
            message = "Username chỉ được chứa chữ, số, dấu chấm, gạch dưới, gạch nối và bắt đầu bằng chữ"
    )
    private String userName;

    @NotBlank(message = "Email không được để trống")
    @Email
    private String email;

    @NotBlank(message = "Password không được để trống")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,128}$",
            message = "Password phải có ít nhất 8 ký tự, gồm chữ và số"
    )
    private String password;

    @Size(max = 100, message = "Display name không được vượt quá 100 ký tự")
    private String displayName;
}