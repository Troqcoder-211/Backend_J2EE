package j2ee.ourteam.interfaces;

import j2ee.ourteam.models.user.CreateUserDTO;
import j2ee.ourteam.models.user.UserDTO;

public interface IUserService {
  public UserDTO createUser(CreateUserDTO userDto);

  // public Object updateUser(Object userDto);
}
