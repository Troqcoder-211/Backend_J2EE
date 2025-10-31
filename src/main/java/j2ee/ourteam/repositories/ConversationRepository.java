package j2ee.ourteam.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findAllByCreatedBy_UserName(String userName);

    @Query("""
        SELECT cm.conversation 
        FROM ConversationMember cm
        WHERE cm.user.id = :userId
    """)
    List<Conversation> findAllByMemberId(UUID userId);
}
