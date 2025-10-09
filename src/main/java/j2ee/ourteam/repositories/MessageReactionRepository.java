package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.MessageReaction;
import j2ee.ourteam.entities.MessageReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, MessageReactionId> {
}
