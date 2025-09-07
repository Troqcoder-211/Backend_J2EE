package j2ee.ourteam.interfaces;

import j2ee.ourteam.models.user.CreateUserDto;
import j2ee.ourteam.models.user.UserDto;

public interface IUserService {
  public UserDto createUser(CreateUserDto userDto);
}
