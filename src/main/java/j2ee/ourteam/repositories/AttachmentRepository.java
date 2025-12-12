package j2ee.ourteam.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
    Page<Attachment> findByConversationId(UUID conversationId, Pageable pageable);
}
