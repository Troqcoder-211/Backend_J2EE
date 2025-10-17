package j2ee.ourteam.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  Page<Notification> findByUser_IdOrderByCreatedAtAtDesc(UUID userId,
      Pageable pageable);
}
