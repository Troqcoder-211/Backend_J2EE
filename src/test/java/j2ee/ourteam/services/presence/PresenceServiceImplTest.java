package j2ee.ourteam.services.presence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import j2ee.ourteam.models.presence.PresenceResponseDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.PresenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PresenceServiceImplTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> ops;
    private ConversationMemberRepository conversationMemberRepository;
    private PresenceServiceImpl presenceService;
    private PresenceRepository presenceRepository;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        ops = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(ops);
        presenceRepository = mock(PresenceRepository.class);

        conversationMemberRepository = mock(ConversationMemberRepository.class);

        presenceService = new PresenceServiceImpl(presenceRepository, redisTemplate, conversationMemberRepository);
    }

    @Test
    void markOnline_shouldSetOnlineWithTTL() {
        String userId = "user1";
        presenceService.markOnline(userId);

        verify(ops, times(1)).set("presence:" + userId, "online", PresenceServiceImpl.TTL);
    }

    @Test
    void markOffline_shouldSetOfflineWithTTL() {
        String userId = "user1";
        presenceService.markOffline(userId);

        verify(ops, times(1)).set("presence:" + userId, "offline", PresenceServiceImpl.TTL);
    }

    @Test
    void refreshTtl_shouldCallExpire() {
        String userId = "user1";
        presenceService.refreshTtl(userId);

        verify(redisTemplate, times(1)).expire("presence:" + userId, PresenceServiceImpl.TTL);
    }

    @Test
    void getStatuses_shouldReturnPresenceMap() {
        UUID currentUser = UUID.randomUUID();
        UUID userA = UUID.randomUUID();
        UUID userB = UUID.randomUUID();

        when(conversationMemberRepository.findRelatedUserIdsByUserId(currentUser))
                .thenReturn(List.of(userA, userB));
        when(ops.get("presence:" + userA)).thenReturn("online");
        when(ops.get("presence:" + userB)).thenReturn(null);

        PresenceResponseDTO result = presenceService.getStatuses(currentUser);

        Map<String, String> expected = Map.of(
                userA.toString(), "online",
                userB.toString(), "offline"
        );

        assertEquals(expected, result.getPresence());
    }

    @Test
    void publishPresenceUpdate_shouldSendMessage() throws JsonProcessingException {
        String userId = "user1";
        String status = "online";

        presenceService.publishPresenceUpdate(userId, status);

        ArgumentCaptor<String> channelCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(redisTemplate).convertAndSend(channelCaptor.capture(), messageCaptor.capture());

        assertEquals("presence_updates", channelCaptor.getValue());
        assertTrue(messageCaptor.getValue().contains(userId));
        assertTrue(messageCaptor.getValue().contains(status));
    }

    @Test
    void key_shouldReturnCorrectKey() {
        String userId = "user1";
        String key = presenceService.key(userId);
        assertEquals("presence:" + userId, key);
    }
}
