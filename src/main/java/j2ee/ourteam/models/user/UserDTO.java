package j2ee.ourteam.models.user;

import java.sql.Date;

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
public class UserDTO {
  private String Id;

  private String fulName;

  private String email;

  private Date dateOfBirth;
}
