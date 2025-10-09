package j2ee.ourteam.services.conversationmember;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.repositories.ConversationMemberRepository;

@Service
public class ConversationMemberServiceImpl implements IConversationMemberService {

  private final ConversationMemberRepository repo;

  public ConversationMemberServiceImpl(ConversationMemberRepository repo) {
    this.repo = repo;
  }

  @Override
  public List<ConversationMember> findAll() {
    return repo.findAll();
  }

  @Override
  public Optional<ConversationMember> findById(ConversationMemberId id) {
    return repo.findById(id);
  }

  @Override
  public ConversationMember save(ConversationMember entity) {
    return repo.save(entity);
  }

  @Override
  public void deleteById(ConversationMemberId id) {
    repo.deleteById(id);
  }

}
