package j2ee.ourteam.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import j2ee.ourteam.entities.Message;

public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    Optional<Message> findTopByConversationIdOrderByCreatedAtDesc(UUID conversationId);
}
