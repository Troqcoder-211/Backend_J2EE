package j2ee.ourteam.services.bot;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.models.bot.MCPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MCPClientTest extends BaseTest {

    private MCPClient mcpClient;
    private WebClient webClientMock;

    @BeforeEach
    void setUp() throws Exception {
        webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);
        mcpClient = new MCPClient("fake-api-key");

        // Inject mock WebClient vào MCPClient
        var field = MCPClient.class.getDeclaredField("webClient");
        field.setAccessible(true);
        field.set(mcpClient, webClientMock);
    }

    @Test
    void chat_success() {
        List<Map<String, String>> messages = List.of(Map.of("role", "user", "content", "Hello"));

        Map<String, Object> fakeResponse = Map.of(
                "choices", List.of(Map.of(
                        "message", Map.of("content", "Hello user!")
                ))
        );

        when(webClientMock.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any())
                .retrieve()
                .bodyToMono(Map.class))
                .thenReturn(Mono.just(fakeResponse));

        MCPResponse response = mcpClient.chat(messages);

        assertThat(response).isNotNull();
        assertThat(response.getText()).isEqualTo("Hello user!");
        assertThat(response.getConfidence()).isEqualTo(1.0);
        assertThat(response.getSuggestedReplies()).isEmpty();
    }

    @Test
    void chat_error_returns_errorResponse() {
        List<Map<String, String>> messages = List.of(Map.of("role", "user", "content", "Hello"));

        when(webClientMock.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any())
                .retrieve()
                .bodyToMono(Map.class))
                .thenReturn(Mono.error(new RuntimeException("API down")));

        MCPResponse response = mcpClient.chat(messages);

        assertThat(response).isNotNull();
        assertThat(response.getText()).contains("⚠️");
        assertThat(response.getConfidence()).isEqualTo(0.0);
    }
}
