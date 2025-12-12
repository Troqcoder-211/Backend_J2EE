package j2ee.ourteam.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import j2ee.ourteam.entities.Message;

public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);

    List<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    @Override
    @EntityGraph(attributePaths = { "attachments", "sender", "replyTo" })
    Page<Message> findAll(Specification<Message> spec, Pageable pageable);
}
