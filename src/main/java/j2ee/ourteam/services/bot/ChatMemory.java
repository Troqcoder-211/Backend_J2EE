package j2ee.ourteam.services.bot;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("customChatMemory")
public class ChatMemory {
    private final Map<String, List<Map<String, String>>> conversations = new ConcurrentHashMap<>();

    public List<Map<String, String>> getMessages(String conversationId) {
        return conversations.computeIfAbsent(conversationId, k -> new ArrayList<>());
    }

    public void addMessage(String conversationId, String role, String content) {
        getMessages(conversationId).add(Map.of("role", role, "content", content));
    }
}
