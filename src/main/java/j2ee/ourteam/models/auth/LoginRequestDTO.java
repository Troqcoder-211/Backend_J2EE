package j2ee.ourteam.models.auth;

import jakarta.validation.constraints.NotBlank;
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
public class LoginRequestDTO {
    @NotBlank(message = "Username không được để trống")
    private String userName;
    @NotBlank(message = "Password không được để trống")
    private String password;
    @NotBlank(message = "Loại thiết bị không được để trống")
    private String deviceType;
    @NotBlank(message = "Push token không được để trống")
    private String pushToken;
}
