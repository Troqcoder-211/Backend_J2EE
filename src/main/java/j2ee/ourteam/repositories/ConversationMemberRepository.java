package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
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

    @Query("""
        SELECT c
        FROM Conversation c
        WHERE c.id IN (
            SELECT cm.conversation.id
            FROM ConversationMember cm
            WHERE cm.user.id IN :userIds
            GROUP BY cm.conversation.id
            HAVING COUNT(cm.user.id) = :size
        )
        AND SIZE(c.members) = :size
    """)
    List<Conversation> findConversationWithExactMembers(
            @Param("userIds") List<java.util.UUID> userIds,
            @Param("size") long size
    );

    @Query("""
        SELECT c
        FROM Conversation c
        JOIN c.members m1
        JOIN c.members m2
        WHERE m1.user.id = :user1
          AND m2.user.id = :user2
          AND SIZE(c.members) = 2
    """)
    Optional<Conversation> findExistingDm(
            @Param("user1") UUID user1,
            @Param("user2") UUID user2
    );

    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);
}
