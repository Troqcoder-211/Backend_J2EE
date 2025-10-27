package j2ee.ourteam.models.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class ChangePasswordRequestDTO {
    @NotBlank(message = "Password không được để trống")
    private String oldPassword;

    @NotBlank(message = "Password không được để trống")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,128}$",
            message = "Password phải có ít nhất 8 ký tự, gồm chữ và số"
    )
    private String newPassword;
}
