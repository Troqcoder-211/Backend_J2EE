package j2ee.ourteam.controllers;

import j2ee.ourteam.services.presence.IPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class HeartbeatController {

    private IPresenceService presenceService;

    @MessageMapping("/heartbeat")
    public void heartbeat(Principal principal, @Payload(required = false) String payload) {
        if (principal == null) return;
        String userId = principal.getName();
        presenceService.refreshTtl(userId);
    }
}