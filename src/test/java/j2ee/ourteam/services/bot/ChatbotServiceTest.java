package j2ee.ourteam.services.bot;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.models.bot.MCPResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChatbotServiceTest extends BaseTest {

    private MCPClient mcpClient;
    private ChatMemory chatMemory;
    private ChatbotService chatbotService;

    @BeforeEach
    void setup() {
        mcpClient = mock(MCPClient.class);
        chatMemory = mock(ChatMemory.class);

        chatbotService = new ChatbotService(mcpClient, chatMemory);
    }

    @Test
    void handleMessage_success() {
        // Arrange
        UUID conversationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        ChatRequest request = new ChatRequest();
        request.setConversationId(conversationId.toString());
        request.setUserId(userId.toString());
        request.setMessage("Hello bot!");

        // Mock history từ chatMemory
        List<Map<String, String>> history = new ArrayList<>();
        when(chatMemory.getMessages(conversationId.toString()))
                .thenReturn(history);

        // Mock MCP response
        MCPResponse mcpResponse = new MCPResponse();
        mcpResponse.setText("Hello user!");
        mcpResponse.setSuggestedReplies(Arrays.asList("Hi", "Hey"));

        when(mcpClient.send(history)).thenReturn(mcpResponse);

        // Act
        ChatResponse response = chatbotService.handleMessage(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getReply()).isEqualTo("Hello user!");
        assertThat(response.getQuickReplies()).containsExactly("Hi", "Hey");

        // Verify chatMemory interactions
        verify(chatMemory).getMessages(conversationId.toString());
        verify(chatMemory).addMessage(conversationId.toString(), "user", "Hello bot!");
        verify(chatMemory).addMessage(conversationId.toString(), "assistant", "Hello user!");

        // Verify MCPClient call
        verify(mcpClient).send(history);
    }
}
