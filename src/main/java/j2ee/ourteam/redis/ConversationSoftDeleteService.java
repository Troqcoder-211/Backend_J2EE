package j2ee.ourteam.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ConversationSoftDeleteService {

    private final RedisTemplate<String, Object> redis;

    @Autowired
    public ConversationSoftDeleteService(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    private String key(String userId, String conversationId) {
        return "deleted:conversation:" + userId + ":" + conversationId;
    }

    /**
     * Đánh dấu conversation là deleted với user.
     * @param userId ID của user
     * @param conversationId ID của conversation
     */
    public void markDeleted(String userId, String conversationId) {
        // Lưu giá trị "true" dạng String để tương thích serializer
        redis.opsForValue().set(key(userId, conversationId), "true");
        // Nếu muốn đặt TTL, ví dụ 1 năm:
        // redis.opsForValue().set(key(userId, conversationId), "true", Duration.ofDays(365));
    }

    /**
     * Kiểm tra conversation đã bị deleted với user chưa
     */
    public boolean isDeleted(String userId, String conversationId) {
        Object val = redis.opsForValue().get(key(userId, conversationId));
        return "true".equals(val);
    }

    /**
     * Khôi phục conversation đã xóa
     */
    public void restore(String userId, String conversationId) {
        redis.delete(key(userId, conversationId));
    }
}
