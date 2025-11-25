package j2ee.ourteam.services.bot;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.Message;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.models.bot.MCPResponse;
import j2ee.ourteam.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AiChatService {

    private final MCPClient mcpClient;
    private final RagService ragService;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Qualifier("customChatMemory")
    private final ChatMemory chatMemory;

    public ChatResponse chat(ChatRequest request) {
        UUID userId = request.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy hoặc tạo conversation riêng
        Conversation conversation = conversationRepository
                .findByCreatedByIdAndConversationType(userId, Conversation.ConversationType.DM)
                .orElseGet(() -> createAIConversation(user));

        // Lưu tin nhắn user
        Message userMsg = Message.builder()
                .conversation(conversation)
                .sender(user)
                .content(request.getMessage())
                .type(Message.MessageType.TEXT)
                .build();
        messageRepository.save(userMsg);
        chatMemory.addMessage(conversation.getId(), "user", request.getMessage());

        // 🔍 RAG info
        String info = ragService.retrieveInfo(request.getMessage());
        if (!info.isBlank()) {
            chatMemory.addMessage(conversation.getId(), "system", info);
        }

        // 🧠 Gọi MCP
        List<Map<String, String>> messages = chatMemory.getMessages(conversation.getId());
        MCPResponse mcpResponse = mcpClient.chat(messages);

        // Lưu tin nhắn AI
        User aiUser = userRepository.findByUserName("AI_BOT").orElseGet(() -> {
            User bot = User.builder()
                    .userName("AI_BOT")
                    .email("ai_bot@example.com")
                    .password("AI_PASSWORD")
                    .displayName("AI Bot")
                    .build();
            return userRepository.save(bot);
        });

        Message aiMsg = Message.builder()
                .conversation(conversation)
                .sender(aiUser)
                .content(mcpResponse.getText())
                .type(Message.MessageType.TEXT)
                .build();
        messageRepository.save(aiMsg);
        chatMemory.addMessage(conversation.getId(), "assistant", mcpResponse.getText());

        return ChatResponse.builder()
                .reply(mcpResponse.getText())
                .quickReplies(mcpResponse.getSuggestedReplies())
                .build();
    }

    private Conversation createAIConversation(User user) {
        Conversation conv = Conversation.builder()
                .conversationType(Conversation.ConversationType.DM)
                .createdBy(user)
                .name(user.getUserName() + "'s AI")
                .build();
        conversationRepository.save(conv);

        ConversationMember member = ConversationMember.builder()
                .conversation(conv)
                .user(user)
                .role(ConversationMember.Role.OWNER)
                .build();
        memberRepository.save(member);

        return conv;
    }

    public Conversation getOrCreateAIConversation(UUID userId) {
        // Lấy tất cả DM conversation của user
        List<Conversation> conversations = conversationRepository
                .findAllByCreatedByIdAndConversationType(userId, Conversation.ConversationType.DM);

        if (!conversations.isEmpty()) {
            // Nếu có nhiều, lấy cái đầu tiên (hoặc theo createdAt mới nhất)
            return conversations.get(0);
        }

        // Nếu chưa có, tạo mới
        return createAIConversation(userId);
    }


    private Conversation createAIConversation(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conv = Conversation.builder()
                .conversationType(Conversation.ConversationType.DM)
                .createdBy(user)
                .name(user.getUserName() + "'s AI")
                .build();
        conversationRepository.save(conv);

        ConversationMember member = ConversationMember.builder()
                .conversation(conv)
                .user(user)
                .role(ConversationMember.Role.OWNER)
                .build();
        memberRepository.save(member);

        return conv;
    }

}
