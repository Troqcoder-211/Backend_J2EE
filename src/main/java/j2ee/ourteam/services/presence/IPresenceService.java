package j2ee.ourteam.services.presence;

import java.util.Map;
import java.util.UUID;

import j2ee.ourteam.entities.Presence;
import j2ee.ourteam.interfaces.GenericCrudService;
import j2ee.ourteam.models.presence.PresenceResponseDTO;

public interface IPresenceService {
    void markOnline(String userId);
    void markOffline(String userId);
    void refreshTtl(String userId);
    PresenceResponseDTO getStatuses(UUID userId);
    String key(String userId);
    void publishPresenceUpdate(String payload);

}
