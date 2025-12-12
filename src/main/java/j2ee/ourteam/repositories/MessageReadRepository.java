package j2ee.ourteam.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import j2ee.ourteam.entities.MessageRead;
import j2ee.ourteam.entities.MessageReadId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import j2ee.ourteam.models.messageread.MessageReadDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO message_reads (message_id, user_id, read_at) VALUES (:messageId, :userId, :readAt)", nativeQuery = true)
    void insertMessageRead(@Param("messageId") UUID messageId,
                           @Param("userId") UUID userId,
                           @Param("readAt") LocalDateTime readAt);

    @Query("""
    SELECT new j2ee.ourteam.models.messageread.MessageReadDTO(
        mr.id.messageId,
        u.id,
        u.userName,
        u.avatarS3Key,
        mr.readAt
    )
    FROM MessageRead mr
    JOIN mr.user u
    WHERE mr.id.messageId = :messageId
""")
    Page<MessageReadDTO> findByMessageIdAsDTO(@Param("messageId") UUID messageId, Pageable pageable);


    @Query("""
    SELECT m.id FROM Message m
    WHERE m.conversation.id = :conversationId
      AND m.createdAt <= :lastReadAt
      AND m.id NOT IN (
          SELECT mr.id.messageId FROM MessageRead mr WHERE mr.id.userId = :userId
      )
""")
    List<UUID> findUnreadMessageIds(@Param("conversationId") UUID conversationId,
                                    @Param("userId") UUID userId,
                                    @Param("lastReadAt") LocalDateTime lastReadAt);

}

