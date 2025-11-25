package j2ee.ourteam.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import j2ee.ourteam.repositories.PresenceRepository;
import j2ee.ourteam.services.presence.IPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class WebSocketEventListener {


    private final IPresenceService presenceService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final PresenceRepository presenceRepository;


    @EventListener
    @Transactional
    public void handleSessionConnected(SessionConnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) sha.getSessionAttributes().get("userId");
        if (userId == null) return;

        presenceService.markOnline(userId);
        presenceService.publishPresenceUpdate(userId, "online");

        presenceRepository.updatePresence(UUID.fromString(userId), true, LocalDateTime.now());
    }


    @EventListener
    @Transactional
    public void handleSessionDisconnect(SessionDisconnectEvent event) throws JsonProcessingException {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) sha.getSessionAttributes().get("userId");
        if (userId == null) return;

        presenceService.markOffline(userId);
        presenceService.publishPresenceUpdate(userId, "offline");

        presenceRepository.updatePresence(UUID.fromString(userId), false, LocalDateTime.now());
    }
}