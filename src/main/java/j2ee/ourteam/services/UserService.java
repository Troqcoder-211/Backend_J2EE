package j2ee.ourteam.services;

import org.springframework.stereotype.Service;

import j2ee.ourteam.interfaces.IUserService;
import j2ee.ourteam.models.user.CreateUserDto;
import j2ee.ourteam.models.user.UserDto;

@Service
public class UserService implements IUserService {

  @Override
  public UserDto createUser(CreateUserDto userDto) {
    throw new UnsupportedOperationException("Unimplemented method 'createUser'");
  }

}
