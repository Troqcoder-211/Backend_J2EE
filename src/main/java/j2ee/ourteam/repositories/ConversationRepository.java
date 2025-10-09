package j2ee.ourteam.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Conversation;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
