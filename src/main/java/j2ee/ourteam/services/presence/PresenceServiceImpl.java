package j2ee.ourteam.services.presence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import j2ee.ourteam.models.presence.PresenceResponseDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class PresenceServiceImpl implements IPresenceService {

    private final StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> ops;
    private static final Duration TTL = Duration.ofSeconds(60);
    private final ConversationMemberRepository conversationMemberRepository;
    private static final String PRESENCE_KEY_PREFIX = "presence:";

    public PresenceServiceImpl(StringRedisTemplate redisTemplate, ConversationMemberRepository conversationMemberRepository){
        this.redisTemplate = redisTemplate;
        this.conversationMemberRepository = conversationMemberRepository;
        this. ops = redisTemplate.opsForValue();
    }

    @Override
    public void markOnline(String userId) {
        ops.set(key(userId), "online", TTL);
    }

    @Override
    public void markOffline(String userId) {
        ops.set(key(userId), "offline", TTL);
    }

    @Override
    public void refreshTtl(String userId) {
        redisTemplate.expire(key(userId), TTL);
    }

    @Override
    public PresenceResponseDTO getStatuses(UUID userId) {
        List<UUID> related = conversationMemberRepository.findRelatedUserIdsByUserId(userId);

        Map<String, String> map = new HashMap<>();
        for (UUID uid : related) {
            String status = ops.get(PRESENCE_KEY_PREFIX + uid);
            map.put(uid.toString(), status != null ? status : "offline");
        }

        return PresenceResponseDTO.builder()
                .presence(map)
                .build();
    }

    @Override
    public String key(String userId) {
        return PRESENCE_KEY_PREFIX + userId;
    }

    @Override
    public void publishPresenceUpdate(String userId, String status) throws JsonProcessingException {
        Map<String, Object> payload = Map.of(
                "presence", Map.of(userId, status)
        );
        redisTemplate.convertAndSend("presence_updates", new ObjectMapper().writeValueAsString(payload));
    }
}
