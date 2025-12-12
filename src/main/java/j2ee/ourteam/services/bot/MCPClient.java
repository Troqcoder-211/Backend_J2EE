package j2ee.ourteam.services.bot;

import j2ee.ourteam.models.bot.MCPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;

@Slf4j
@Component
public class MCPClient {

    private final WebClient webClient;

    public MCPClient(@Value("${spring.ai.openai.api-key}") String openAiApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + openAiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public MCPResponse chat(List<Map<String, String>> messages) {
        Map<String, Object> payload = Map.of(
                "model", "gpt-4o-mini",
                "messages", messages,
                "temperature", 0.5);

        try {
            Map<String, Object> response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String text = extractText(response);

            return MCPResponse.builder()
                    .text(text)
                    .suggestedReplies(Collections.emptyList())
                    .confidence(1.0)
                    .raw(response)
                    .build();

        } catch (Exception e) {
            log.error("MCPClient error", e);
            return MCPResponse.builder()
                    .text("⚠️ " + e.getMessage())
                    .suggestedReplies(Collections.emptyList())
                    .confidence(0.0)
                    .raw(Collections.emptyMap())
                    .build();
        }
    }

    private String extractText(Map<String, Object> response) {
        if (response == null)
            return "(empty response)";
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty())
            return "(no choices)";
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return message != null ? (String) message.get("content") : "(no message)";
    }
}
