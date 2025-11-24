package j2ee.ourteam.services.bot;

import j2ee.ourteam.BaseTest;
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

/**
 * Unit test MCPClient using mock WebClient chain with RETURNS_DEEP_STUBS.
 */
class MCPClientTest extends BaseTest {

    private MCPClient mcpClient;
    private WebClient webClientMock;

    @BeforeEach
    void setUp() throws Exception {
        // mock WebClient with deep stubs to simulate call chain
        webClientMock = mock(WebClient.class, RETURNS_DEEP_STUBS);

        // create MCPClient (real object)
        mcpClient = new MCPClient("fake-api-key");

        // inject mock WebClient into private field via reflection
        var field = MCPClient.class.getDeclaredField("webClient");
        field.setAccessible(true);
        field.set(mcpClient, webClientMock);
    }

    @Test
    void send_success() {
        List<Map<String, String>> messages =
                List.of(Map.of("role", "user", "content", "Hello"));

        // Fake OpenAI-style response
        Map<String, Object> fakeResponse = Map.of(
                "choices", List.of(Map.of(
                        "message", Map.of("content", "Hello user!")
                ))
        );

        // Mock full WebClient chain
        when(webClientMock.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any())
                .retrieve()
                .bodyToMono(Map.class))
                .thenReturn(Mono.just(fakeResponse));

        // Call service
        var response = mcpClient.send(messages);

        // Validate result
        assertThat(response).isNotNull();
        assertThat(response.getText()).isEqualTo("Hello user!");
        assertThat(response.getConfidence()).isEqualTo(1.0);
        assertThat(response.getSuggestedReplies()).isEmpty();
    }

    @Test
    void send_error_returns_errorResponse() {
        List<Map<String, String>> messages =
                List.of(Map.of("role", "user", "content", "Hello"));

        // Mock API failure: bodyToMono returns an error
        when(webClientMock.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(any())
                .retrieve()
                .bodyToMono(Map.class))
                .thenReturn(Mono.error(new RuntimeException("API down")));

        var response = mcpClient.send(messages);

        // Validate fallback error response
        assertThat(response).isNotNull();
        assertThat(response.getText()).contains("⚠️");
        assertThat(response.getConfidence()).isEqualTo(0.0);
    }
}
