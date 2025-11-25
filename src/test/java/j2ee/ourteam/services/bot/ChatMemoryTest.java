package j2ee.ourteam.services.bot;

import j2ee.ourteam.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMemoryTest extends BaseTest {

    private ChatMemory chatMemory;

    @BeforeEach
    void setUp() {
        chatMemory = new ChatMemory();
    }

    @Test
    void getMessages_newConversation_returnsEmptyList() {
        UUID conversationId = UUID.fromString("6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d");

        List<Map<String, String>> messages = chatMemory.getMessages(conversationId);

        assertThat(messages).isNotNull();
        assertThat(messages).isEmpty();
    }

    @Test
    void addMessage_and_getMessages_returnsCorrectly() {
        UUID conversationId = UUID.fromString("6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d");

        // Add message from user
        chatMemory.addMessage(conversationId, "user", "Hello bot!");

        List<Map<String, String>> messages = chatMemory.getMessages(conversationId);
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0))
                .containsEntry("role", "user")
                .containsEntry("content", "Hello bot!");

        // Add message from assistant
        chatMemory.addMessage(conversationId, "assistant", "Hello user!");

        messages = chatMemory.getMessages(conversationId);
        assertThat(messages).hasSize(2);
        assertThat(messages.get(1))
                .containsEntry("role", "assistant")
                .containsEntry("content", "Hello user!");
    }

    @Test
    void getMessages_sameConversation_returnsSameList() {
        UUID conversationId = UUID.fromString("6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d");

        chatMemory.addMessage(conversationId, "user", "Message 1");
        chatMemory.addMessage(conversationId, "user", "Message 2");

        List<Map<String, String>> messages = chatMemory.getMessages(conversationId);

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).get("content")).isEqualTo("Message 1");
        assertThat(messages.get(1).get("content")).isEqualTo("Message 2");
    }

    @Test
    void getMessages_differentConversations_areIndependent() {
        UUID conv1 = UUID.fromString("6b7c8d9e-1234-4a1b-9c2d-3e4f5a6b7c8d");
        UUID conv2 = UUID.fromString("7c8d9e0f-2345-4b2c-ad3e-4f5a6b7c8d9e");

        chatMemory.addMessage(conv1, "user", "Message conv1");
        chatMemory.addMessage(conv2, "user", "Message conv2");

        assertThat(chatMemory.getMessages(conv1))
                .hasSize(1)
                .extracting(m -> m.get("content"))
                .containsExactly("Message conv1");

        assertThat(chatMemory.getMessages(conv2))
                .hasSize(1)
                .extracting(m -> m.get("content"))
                .containsExactly("Message conv2");
    }
}
