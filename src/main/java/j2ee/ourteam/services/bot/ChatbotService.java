package j2ee.ourteam.services.bot;

import org.springframework.stereotype.Service;

import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.models.bot.MCPRequest;
import j2ee.ourteam.models.bot.MCPResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotService {
  private final MCPClient mcpClient;

  public ChatResponse handleMessage(ChatRequest request) {
    // Gửi context + câu hỏi tới MCP
    MCPRequest mcpRequest = MCPRequest.builder()
        .userId(request.getUserId())
        .conversationId(request.getConversationId())
        .input(request.getMessage())
        .build();

    MCPResponse mcpResponse = mcpClient.send(mcpRequest);

    return ChatResponse.builder()
        .reply(mcpResponse.getText())
        .quickReplies(mcpResponse.getSuggestedReplies())
        .build();
  }
}
