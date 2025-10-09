package j2ee.ourteam.services.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.repositories.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Optional<User> findById(UUID id) {
    return userRepository.findById(id);
  }

  @Override
  public User save(User entity) {
    return userRepository.save(entity);
  }

  @Override
  public void deleteById(UUID id) {
    userRepository.deleteById(id);
  }

}
