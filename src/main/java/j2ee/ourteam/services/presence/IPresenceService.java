package j2ee.ourteam.services.presence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import j2ee.ourteam.models.presence.PresenceResponseDTO;

public interface IPresenceService {
    void markOnline(String userId);

    void markOffline(String userId);

    void refreshTtl(String userId);

    PresenceResponseDTO getStatuses(UUID userId);

    String key(String userId);

    void updatePresence(UUID id, boolean status, LocalDateTime ts);

    void publishPresenceUpdate(String userId, String status) throws JsonProcessingException;

}
