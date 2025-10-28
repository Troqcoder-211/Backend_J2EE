package j2ee.ourteam.repositories;

import j2ee.ourteam.entities.MessageRead;
import j2ee.ourteam.entities.MessageReadId;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReadRepository extends JpaRepository<MessageRead, MessageReadId> {
  Page<MessageRead> findByMessageId(UUID messageId, Pageable pageable);
}
