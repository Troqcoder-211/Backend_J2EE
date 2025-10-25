package j2ee.ourteam.services.presence;

import j2ee.ourteam.models.presence.PresenceResponseDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;


@Service
public class PresenceServiceImpl implements IPresenceService{


    private final StringRedisTemplate redisTemplate;
    private final ValueOperations<String, String> ops;
    private static final Duration TTL = Duration.ofSeconds(60);

    @Autowired
    private ConversationMemberRepository conversationMemberRepository;
    private static final String PRESENCE_KEY_PREFIX = "presence:";

    @Autowired
    public PresenceServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = redisTemplate.opsForValue();
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
        List<UUID> relatedUserIds = conversationMemberRepository.findRelatedUserIdsByUserId(userId);
        List<String> keys = relatedUserIds.stream().map(id -> PRESENCE_KEY_PREFIX + id).toList();

        Map<UUID, String> result = new HashMap<>();
        int index = 0;
        for (String key : keys) {
            String status = redisTemplate.opsForValue().get(key);
            result.put(relatedUserIds.get(index), status != null ? status : "offline");
            index += 1;
        }

        PresenceResponseDTO response;
        response = PresenceResponseDTO.builder().mapPresenceResponse(result).build();
        return response;
    }

    @Override
    public String key(String userId) {
        return "user:" + userId + ":status";
    }

    @Override
    public void publishPresenceUpdate(String payload) {
        redisTemplate.convertAndSend("presence_updates", payload);
    }
}
