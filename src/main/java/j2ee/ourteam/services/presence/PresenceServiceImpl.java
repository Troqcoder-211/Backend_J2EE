package j2ee.ourteam.services.presence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Presence;
import j2ee.ourteam.repositories.PresenceRepository;

@Service
public class PresenceServiceImpl implements IPresenceService {

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

}
