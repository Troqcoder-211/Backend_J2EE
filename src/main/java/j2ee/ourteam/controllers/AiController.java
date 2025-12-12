package j2ee.ourteam.controllers;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.services.bot.AiChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> getOrCreateConversation(@RequestBody Map<String, String> payload) {
        String userIdStr = payload.get("userId");
        if (userIdStr == null || userIdStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
        }

        try {
            UUID userId = UUID.fromString(userIdStr);


            // Gọi Service (đã update public method)
            Conversation conversation = aiChatService.getOrCreateAIConversation(userId);

            // Trả về thông tin cần thiết cho Frontend
            Map<String, Object> response = new HashMap<>();
            response.put("id", conversation.getId());
            response.put("name", conversation.getName());
            response.put("type", conversation.getConversationType());
            response.put("avatar", "https://ui-avatars.com/api/?name=AI&background=0D8ABC&color=fff"); // Avatar giả lập cho AI

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid UUID format"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}
