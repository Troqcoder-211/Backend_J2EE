package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.MessageRead;
import j2ee.ourteam.entities.MessageReadId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {
}
