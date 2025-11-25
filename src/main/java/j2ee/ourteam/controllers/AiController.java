package j2ee.ourteam.controllers;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.services.bot.AiChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class AiController {

    private final AiChatService aiChatService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.chat(request));
    }

    @PostMapping("/conversation")
    public Map<String, Object> getOrCreateConversation(@RequestBody Map<String, String> payload) {
        String userIdStr = payload.get("userId");
        if (userIdStr == null) throw new RuntimeException("userId is required");

        UUID userId = UUID.fromString(userIdStr);
        Conversation conversation = aiChatService.getOrCreateAIConversation(userId);

        return Map.of(
                "id", conversation.getId(),
                "name", conversation.getName()
        );
    }

}
