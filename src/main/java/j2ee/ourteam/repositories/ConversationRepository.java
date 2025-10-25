package j2ee.ourteam.repositories;

import java.util.List;
import java.util.UUID;

import j2ee.ourteam.entities.ConversationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Conversation;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
}
