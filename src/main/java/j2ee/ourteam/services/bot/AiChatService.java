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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    @Transactional
    public ChatResponse chat(ChatRequest request) {
        UUID userId = request.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ 1. Lấy hoặc tạo cuộc hội thoại AI (Đảm bảo chỉ có 1 cái duy nhất)
        Conversation conversation = getOrCreateAIConversation(user.getId());

        // ✅ 2. Lưu tin nhắn của User
        Message userMsg = Message.builder()
                .conversation(conversation)
                .sender(user)
                .content(request.getMessage())
                .type(Message.MessageType.TEXT)
                .build();
        messageRepository.save(userMsg);
        chatMemory.addMessage(conversation.getId(), "user", request.getMessage());

        // 🔍 3. RAG info (Tìm kiếm thông tin liên quan)
        String info = ragService.retrieveInfo(request.getMessage());
        if (info != null && !info.isBlank()) {
            chatMemory.addMessage(conversation.getId(), "system", info);
        }

        // 🧠 4. Gọi MCP (AI xử lý)
        List<Map<String, String>> messages = chatMemory.getMessages(conversation.getId());
        MCPResponse mcpResponse = mcpClient.chat(messages);

        // ✅ 5. Lấy User Bot (để lưu tin nhắn trả về)
        User aiUser = getOrCreateAiBotUser();

        // ✅ 6. Lưu tin nhắn phản hồi của AI
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

    /**
     * Hàm quan trọng: Tìm cuộc hội thoại AI cũ, nếu chưa có thì tạo mới.
     * Đảm bảo mỗi user chỉ có 1 cuộc hội thoại loại AI.
     */
    public Conversation getOrCreateAIConversation(UUID userId) {
        // Tìm conversation do user tạo và có type là AI
        // Lưu ý: Bạn cần đảm bảo Repository có hàm findFirstByCreatedBy_IdAndConversationType hoặc tương tự
        Optional<User> user = userRepository.findById(userId);
        return conversationRepository
                .findFirstByCreatedByIdAndConversationType(user.get().getId(), Conversation.ConversationType.AI)
                .orElseGet(() -> createNewAIConversation(user.get()));
    }

    /**
     * Logic tạo mới cuộc hội thoại AI
     */
    private Conversation createNewAIConversation(User user) {
        // 1. Tạo Conversation
        Conversation conv = Conversation.builder()
                .conversationType(Conversation.ConversationType.AI) // Đánh dấu là AI
                .createdBy(user)
                .name("AI Assistant") // Đặt tên cố định hoặc theo user
                .build();
        conv = conversationRepository.save(conv);

        // 2. Thêm User vào cuộc hội thoại
        ConversationMember userMember = ConversationMember.builder()
                .conversation(conv)
                .user(user)
                .role(ConversationMember.Role.OWNER)
                .build();
        memberRepository.save(userMember);

        // 3. Thêm AI Bot vào cuộc hội thoại (Optional - nhưng nên có để hiển thị avatar bot)
        User aiUser = getOrCreateAiBotUser();
        ConversationMember botMember = ConversationMember.builder()
                .conversation(conv)
                .user(aiUser)
                .role(ConversationMember.Role.MEMBER)
                .build();
        memberRepository.save(botMember);

        return conv;
    }

    /**
     * Helper: Lấy user BOT từ DB, nếu chưa có thì tạo mới
     */
    private User getOrCreateAiBotUser() {
        return userRepository.findByUserName("AI_BOT").orElseGet(() -> {
            User bot = User.builder()
                    .userName("AI_BOT")
                    .email("ai_bot@ourteam.com")
                    .password("SECURE_AI_PASSWORD_HASH") // Nên encode password này
                    .displayName("AI Assistant")
                    .role("USER") // Hoặc Role.BOT nếu bạn có enum đó
                    .isDisabled(false)
                    .avatarS3Key("https://ui-avatars.com/api/?name=AI&background=0D8ABC&color=fff") // Avatar mặc định
                    .build();
            return userRepository.save(bot);
        });
    }
}