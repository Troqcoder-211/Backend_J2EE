package j2ee.ourteam.controllers;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.presence.PresenceResponseDTO;
import j2ee.ourteam.services.presence.IPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/presence")
@RequiredArgsConstructor
public class PresenceController {
    private final IPresenceService presenceService;

    @GetMapping("/statuses")
    public ResponseEntity<?> getStatuses(@AuthenticationPrincipal User currentUser) {
        try {
            PresenceResponseDTO presenceResponseDTO = presenceService.getStatuses(currentUser.getId());
            return ResponseEntity.ok().body(presenceResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}