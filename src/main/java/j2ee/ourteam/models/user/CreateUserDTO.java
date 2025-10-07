package j2ee.ourteam.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserDTO {
  @NotBlank(message = "Username khong duoc trong")
  private String username;

  @Email(message = "Email khong hop le")
  private String email;

  @Size(min = 6, message = "Mat khau phai co it nhat 6 ky tu")
  private String password;
}
