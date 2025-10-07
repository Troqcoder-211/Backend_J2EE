package j2ee.ourteam.services;

import org.springframework.stereotype.Service;

import j2ee.ourteam.interfaces.IUserService;
import j2ee.ourteam.models.user.CreateUserDTO;
import j2ee.ourteam.models.user.UserDTO;

@Service
public class UserService implements IUserService {

  @Override
  public UserDTO createUser(CreateUserDTO userDto) {
    throw new UnsupportedOperationException("Unimplemented method 'createUser'");
  }

}
