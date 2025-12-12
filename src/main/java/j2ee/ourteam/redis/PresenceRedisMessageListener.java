package j2ee.ourteam.redis;

// import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Configuration
@RequiredArgsConstructor
public class PresenceRedisMessageListener {

    // private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ConversationMemberRepository conversationMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        container.addMessageListener((Message message, byte[] pattern) -> {
            try {
                String json = new String(message.getBody());

                // parse JSON { "presence": { "uuid": "online" } }
                Map<?, ?> payload = objectMapper.readValue(json, Map.class);

                Map<?, ?> presence = (Map<?, ?>) payload.get("presence");

                List<String> targets = (List<String>) payload.get("targets");

                if (targets != null) {
                    for (String target : targets) {
                        messagingTemplate.convertAndSend("/topic/"+target+"/presence", payload);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error parsing presence update: " + e.getMessage());
            }
        }, new ChannelTopic("presence_updates"));

        return container;
    }
}
