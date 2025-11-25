//package j2ee.ourteam.services.bot;
//
//import j2ee.ourteam.models.bot.MCPRequest;
//import j2ee.ourteam.models.bot.MCPResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import java.util.*;
//
//@Slf4j
//@Component
//public class MCPClient {
//
//  private final WebClient webClient;
//
//  public MCPClient(@Value("${spring.ai.openai.api-key}") String openAiApiKey) {
//    this.webClient = WebClient.builder()
//        .baseUrl("https://api.openai.com/v1/chat/completions")
//        .defaultHeader("Authorization", "Bearer " + openAiApiKey)
//        .defaultHeader("Content-Type", "application/json")
//        .build();
//  }
//
//  public MCPResponse send(List<Map<String, String>> messages) {
//    Map<String, Object> payload = Map.of(
//        "model", "gpt-4o-mini",
//        "messages", messages,
//        "temperature", 0.5);
//
//    try {
//      Map<String, Object> response = webClient.post()
//          .contentType(MediaType.APPLICATION_JSON)
//          .bodyValue(payload)
//          .retrieve()
//          .bodyToMono(Map.class)
//          .block();
//
//      String text = extractText(response);
//
//      return MCPResponse.builder()
//          .text(text)
//          .suggestedReplies(Collections.emptyList())
//          .confidence(1.0)
//          .raw(response)
//          .build();
//    } catch (Exception e) {
//      return errorResponse("⚠️ " + e.getMessage());
//    }
//  }
//
//  public MCPResponse sendv3(MCPRequest request) {
//    String currentDate = java.time.LocalDate.now().toString();
//
//    List<Map<String, Object>> messages = new ArrayList<>();
//    messages.add(Map.of("role", "system", "content",
//        "Bạn là một trợ lý AI giúp người dùng trả lời chính xác, có thể sử dụng thời gian hiện tại là " + currentDate +
//            " (định dạng yyyy-MM-dd). Khi người dùng hỏi về ngày tháng, hãy dựa theo ngày hiện tại này. Trả lời bằng tiếng Việt rõ ràng."));
//    messages.add(Map.of("role", "user", "content", request.getInput()));
//
//    Map<String, Object> payload = Map.of(
//        "model", "gpt-4o-mini",
//        "messages", messages,
//        "temperature", 0.5);
//
//    int maxRetries = 3;
//    for (int attempt = 1; attempt <= maxRetries; attempt++) {
//      try {
//        Map<String, Object> response = webClient.post()
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(payload)
//            .retrieve()
//            .bodyToMono(Map.class)
//            .block();
//
//        String text = extractText(response);
//
//        return MCPResponse.builder()
//            .text(text)
//            .suggestedReplies(Collections.emptyList())
//            .confidence(1.0)
//            .raw(response)
//            .build();
//
//      } catch (WebClientResponseException e) {
//        if (e.getStatusCode().value() == 429 && attempt < maxRetries) {
//          log.warn("⚠️ Rate limited by OpenAI. Retrying... (attempt {}/{})", attempt, maxRetries);
//          try {
//            Thread.sleep(2000L * attempt);
//          } catch (InterruptedException ignored) {
//          }
//          continue;
//        }
//        log.error("❌ OpenAI API error: {}", e.getResponseBodyAsString());
//        return errorResponse("OpenAI API error: " + e.getStatusCode() + " " + e.getMessage());
//      } catch (Exception e) {
//        log.error("❌ MCPClient unexpected error", e);
//        return errorResponse("Unexpected error: " + e.getMessage());
//      }
//    }
//
//    return errorResponse("⚠️ OpenAI rate limit. Please try again later.");
//  }
//
//  private String extractText(Map<String, Object> response) {
//    if (response == null)
//      return "(empty response)";
//    var choices = (List<Map<String, Object>>) response.get("choices");
//    if (choices == null || choices.isEmpty())
//      return "(no choices)";
//    var message = (Map<String, Object>) choices.get(0).get("message");
//    return message != null ? (String) message.get("content") : "(no message)";
//  }
//
//  private MCPResponse errorResponse(String message) {
//    return MCPResponse.builder()
//        .text("⚠️ " + message)
//        .suggestedReplies(Collections.emptyList())
//        .confidence(0.0)
//        .raw(Collections.emptyMap())
//        .build();
//  }
//
//  public MCPResponse sendV1(MCPRequest request) {
//    try {
//      Map<String, Object> payload = Map.of(
//          "model", "gpt-4o-mini", // gợi ý dùng model nhẹ để tránh rate limit
//          "messages", List.of(Map.of("role", "user", "content", request.getInput())),
//          "temperature", 0.7);
//
//      Map<String, Object> response = webClient.post()
//          .contentType(MediaType.APPLICATION_JSON)
//          .bodyValue(payload)
//          .retrieve()
//          .bodyToMono(Map.class)
//          .block();
//
//      // Lấy nội dung trả về
//      String text = "";
//      if (response != null) {
//        var choices = (java.util.List<Map<String, Object>>) response.get("choices");
//        if (choices != null && !choices.isEmpty()) {
//          Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
//          text = (String) message.get("content");
//        }
//      }
//
//      return MCPResponse.builder()
//          .text(text != null ? text.trim() : "")
//          .suggestedReplies(Collections.emptyList())
//          .confidence(1.0)
//          .raw(response)
//          .build();
//
//    } catch (Exception e) {
//      return MCPResponse.builder()
//          .text("⚠️ MCPClient error: " + e.getMessage())
//          .suggestedReplies(Collections.emptyList())
//          .confidence(0.0)
//          .raw(Collections.emptyMap())
//          .build();
//    }
//  }
//}


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
                "temperature", 0.5
        );

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
        if (response == null) return "(empty response)";
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) return "(no choices)";
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return message != null ? (String) message.get("content") : "(no message)";
    }
}
