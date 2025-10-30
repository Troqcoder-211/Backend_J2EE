package j2ee.ourteam.services.bot;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.models.bot.MCPRequest;
import j2ee.ourteam.models.bot.MCPResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ChatbotService {
  private final MCPClient mcpClient;

    @Autowired
    @Qualifier("customChatMemory")
    private final ChatMemory chatMemory;

  public ChatResponse handleMessage(ChatRequest request) {
      List<Map<String, String>> messages = chatMemory.getMessages(request.getConversationId());

      // thêm tin nhắn người dùng vào memory
      chatMemory.addMessage(request.getConversationId(), "user", request.getMessage());

    // Gửi context + câu hỏi tới MCP
    MCPRequest mcpRequest = MCPRequest.builder()
        .userId(request.getUserId())
        .conversationId(request.getConversationId())
        .input(request.getMessage())
        .build();

    MCPResponse mcpResponse = mcpClient.send(messages);

      chatMemory.addMessage(request.getConversationId(), "assistant", mcpResponse.getText());

      return ChatResponse.builder()
        .reply(mcpResponse.getText())
        .quickReplies(mcpResponse.getSuggestedReplies())
        .build();
  }

  public ChatResponse chatWithBot(ChatRequest chatRequest){
      List<Map<String, String>> messages = chatMemory.getMessages(chatRequest.getConversationId());

      MCPRequest mcpRequest =  MCPRequest.builder()
              .userId(chatRequest.getUserId())
              .conversationId(chatRequest.getConversationId())
              .input(chatRequest.getMessage())
              .build();

      MCPResponse mcpResponse = mcpClient.send(messages);

    return null;
  }
}
