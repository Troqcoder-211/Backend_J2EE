package j2ee.ourteam.services.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<Object> findAll() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

  @Override
  public Optional<Object> findById(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public Object create(Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  public Object update(UUID id, Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void deleteById(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

  // private final UserRepository userRepository;

  // public UserServiceImpl(UserRepository userRepository) {
  // this.userRepository = userRepository;
  // }

  // @Override
  // public List<User> findAll() {
  // return userRepository.findAll();
  // }

  // @Override
  // public Optional<User> findById(UUID id) {
  // return userRepository.findById(id);
  // }

  // @Override
  // public User save(User entity) {
  // return userRepository.save(entity);
  // }

  // @Override
  // public void deleteById(UUID id) {
  // userRepository.deleteById(id);
  // }

}
