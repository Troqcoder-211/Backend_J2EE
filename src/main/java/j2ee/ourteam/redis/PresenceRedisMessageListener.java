package j2ee.ourteam.redis;

// import com.fasterxml.jackson.databind.ObjectMapper;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class PresenceRedisMessageListener {

    // private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConversationMemberRepository conversationMemberRepository;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // Đăng ký listener cho channel "presence_updates"
        container.addMessageListener((Message message, byte[] pattern) -> {
            try {
                String body = new String(message.getBody());
                // Expected format: userId:online or userId:offline
                String[] parts = body.split(":", 2);
                if (parts.length < 2)
                    return;

                String userId = parts[0];
                String status = parts[1];

                List<UUID> relatedUserIds = conversationMemberRepository
                        .findRelatedUserIdsByUserId(UUID.fromString(userId));

                // ✅ Gửi cập nhật presence tới từng user có liên quan
                Map<String, String> payload = Map.of("userId", userId, "status", status);
                for (UUID uid : relatedUserIds) {
                    simpMessagingTemplate.convertAndSendToUser(uid.toString(), "/queue/presence", payload);
                }

            } catch (Exception e) {
                System.err.println("Failed to process presence update: " + e.getMessage());
            }
        }, new ChannelTopic("presence_updates"));

        return container;
    }
}
