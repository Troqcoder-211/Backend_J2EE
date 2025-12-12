package j2ee.ourteam.services.reaction;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.MessageReactionId;

@Service
public class MessageReactionServiceImpl implements IMessageReactionService {

  @Override
  public List<Object> findAll() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

  @Override
  public Optional<Object> findById(MessageReactionId id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public Object create(Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  public Object update(MessageReactionId id, Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void deleteById(MessageReactionId id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

}
