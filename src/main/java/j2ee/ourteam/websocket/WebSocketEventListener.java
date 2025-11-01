package j2ee.ourteam.websocket;

import j2ee.ourteam.repositories.PresenceRepository;
import j2ee.ourteam.services.presence.IPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
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
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = sha.getSessionAttributes();
        if (attrs == null) return;
        Object userIdObj = attrs.get("userId");
        if (userIdObj == null) return;
        String userId = userIdObj.toString();


// mark online in redis
        presenceService.markOnline(userId);
// publish to other instances
        presenceService.publishPresenceUpdate(userId + ":online");


        presenceRepository.updatePresence(UUID.fromString(userId), true, LocalDateTime.now());
// send initial presence list to the connected user (example)
// you can fetch conversation participants and their statuses
        simpMessagingTemplate.convertAndSendToUser(userId, "/queue/presence", Map.of("type","connected"));
    }


    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = sha.getSessionAttributes();
        if (attrs == null) return;
        Object userIdObj = attrs.get("userId");
        if (userIdObj == null) return;
        String userId = userIdObj.toString();


// mark offline (debounce logic can be added)
        presenceService.markOffline(userId);
        presenceService.publishPresenceUpdate(userId + ":offline");


        presenceRepository.updatePresence(UUID.fromString(userId), false, LocalDateTime.now());
    }
}