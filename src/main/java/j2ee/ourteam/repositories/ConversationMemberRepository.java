package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {
    List<ConversationMember> findByIdConversationId (UUID conversationId);
}
