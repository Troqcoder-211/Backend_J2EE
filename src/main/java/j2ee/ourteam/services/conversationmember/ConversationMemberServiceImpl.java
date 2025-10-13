package j2ee.ourteam.services.conversationmember;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.repositories.ConversationMemberRepository;

@Service
public class ConversationMemberServiceImpl implements IConversationMemberService {

  @Override
  public List<Object> findAll() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

  @Override
  public Optional<Object> findById(ConversationMemberId id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public Object create(Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  public Object update(ConversationMemberId id, Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void deleteById(ConversationMemberId id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

}
