package j2ee.ourteam.repositories;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import j2ee.ourteam.entities.Presence;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface PresenceRepository extends JpaRepository<Presence, UUID> {
    @Modifying
    @Query("UPDATE Presence p SET p.isOnline = :status, p.lastSeenAt = :ts, p.updatedAt = CURRENT_TIMESTAMP WHERE p.userId = :id")
    int updatePresence(UUID id, boolean status, LocalDateTime ts);
}
