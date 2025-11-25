package j2ee.ourteam.services.bot;

import j2ee.ourteam.entities.*;
import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.models.bot.MCPResponse;
import j2ee.ourteam.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AiChatServiceTest {

  private MCPClient mcpClient;
  private RagService ragService;
  private ConversationRepository conversationRepository;
  private ConversationMemberRepository memberRepository;
  private MessageRepository messageRepository;
  private UserRepository userRepository;
  private ChatMemory chatMemory;

  private AiChatService aiChatService;

  @BeforeEach
  void setup() {
    mcpClient = mock(MCPClient.class);
    ragService = mock(RagService.class);
    conversationRepository = mock(ConversationRepository.class);
    memberRepository = mock(ConversationMemberRepository.class);
    messageRepository = mock(MessageRepository.class);
    userRepository = mock(UserRepository.class);
    chatMemory = mock(ChatMemory.class);

    aiChatService = new AiChatService(
        mcpClient,
        ragService,
        conversationRepository,
        memberRepository,
        messageRepository,
        userRepository,
        chatMemory);
  }

  @Test
  void chat_success() {
    // Arrange
    UUID userId = UUID.randomUUID();
    ChatRequest req = new ChatRequest();
    req.setUserId(userId);
    req.setMessage("Hello AI!");

    // User tồn tại
    User user = User.builder()
        .id(userId)
        .userName("john")
        .email("john@example.com")
        .build();
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    // Conversation AI đã tồn tại
    Conversation conv = Conversation.builder()
        .id(UUID.randomUUID())
        .conversationType(Conversation.ConversationType.DM)
        .createdBy(user)
        .name("john's AI")
        .build();
    when(conversationRepository
        .findByCreatedByIdAndConversationType(userId, Conversation.ConversationType.DM))
        .thenReturn(Optional.of(conv));

    // RAG trả lời
    when(ragService.retrieveInfo("Hello AI!")).thenReturn("");

    // ChatMemory history
    List<Map<String, String>> history = new ArrayList<>();
    when(chatMemory.getMessages(conv.getId())).thenReturn(history);

    // MCP trả lời
    MCPResponse mcpResponse = new MCPResponse();
    mcpResponse.setText("Hello human!");
    mcpResponse.setSuggestedReplies(List.of("Hi", "What's up"));
    when(mcpClient.chat(history)).thenReturn(mcpResponse);

    // Act
    ChatResponse response = aiChatService.chat(req);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getReply()).isEqualTo("Hello human!");
    assertThat(response.getQuickReplies()).contains("Hi");

    // Verify memory interactions
    verify(chatMemory).addMessage(conv.getId(), "user", "Hello AI!");
    verify(chatMemory).addMessage(conv.getId(), "assistant", "Hello human!");

    // Verify MCP call
    verify(mcpClient).chat(history);

    // Verify saving messages
    verify(messageRepository, times(2)).save(any(Message.class));
  }
}
