package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {
    List<ConversationMember> findByIdConversationId(UUID conversationId);

    @Query("""
                SELECT DISTINCT cm.user.id
                FROM ConversationMember cm
                WHERE cm.conversation.id IN (
                    SELECT cm2.conversation.id
                    FROM ConversationMember cm2
                    WHERE cm2.user.id = :userId
                )
            """)
    List<UUID> findRelatedUserIdsByUserId(@Param("userId") UUID userId);

}
