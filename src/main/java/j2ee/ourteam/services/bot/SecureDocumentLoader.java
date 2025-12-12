package j2ee.ourteam.services.bot;

import j2ee.ourteam.entities.*;
import j2ee.ourteam.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecureDocumentLoader implements CommandLineRunner {

    private final VectorStore vectorStore;

    private final UserRepository users;
    private final MessageRepository messages;
    private final ConversationRepository conversations;
    private final ConversationMemberRepository members;
    private final AttachmentRepository attachments;
    private final MessageReactionRepository reactions;
    private final MessageReadRepository reads;
    private final NotificationRepository notifications;
    private final DeviceRepository devices;
    private final PresenceRepository presences;
    private final PasswordResetOtpRepository otps;
    private final RefreshTokenRepository tokens;

    @Override
    public void run(String... args) {
        List<Document> docs = new ArrayList<>();

        users.findAll().forEach(u ->
                docs.add(new Document("User[id=%s, username=%s, display=%s]".formatted(
                        u.getId(), u.getUserName(), u.getDisplayName()
                )))
        );

        conversations.findAll().forEach(c ->
                docs.add(new Document("Conversation[id=%s, type=%s, name=%s, createdBy=%s]".formatted(
                        c.getId(), c.getConversationType(), c.getName(),
                        c.getCreatedBy() != null ? c.getCreatedBy().getId() : null
                )))
        );

        members.findAll().forEach(m ->
                docs.add(new Document("ConversationMember[userId=%s, conversationId=%s, role=%s]".formatted(
                        m.getUser() != null ? m.getUser().getId() : null,
                        m.getConversation() != null ? m.getConversation().getId() : null,
                        m.getRole()
                )))
        );

        messages.findAll().forEach(msg ->
                docs.add(new Document("Message[id=%s, conversationId=%s, senderId=%s, type=%s, content=%s]".formatted(
                        msg.getId(),
                        msg.getConversation() != null ? msg.getConversation().getId() : null,
                        msg.getSender() != null ? msg.getSender().getId() : null,
                        msg.getType(),
                        msg.getContent()
                )))
        );

        attachments.findAll().forEach(att ->
                docs.add(new Document("Attachment[id=%s, uploaderId=%s, filename=%s]".formatted(
                        att.getId(),
                        att.getUploader() != null ? att.getUploader().getId() : null,
                        att.getFilename()
                )))
        );

        reactions.findAll().forEach(r ->
                docs.add(new Document("MessageReaction[messageId=%s, userId=%s, emoji=%s]".formatted(
                        r.getMessage() != null ? r.getMessage().getId() : null,
                        r.getUser() != null ? r.getUser().getId() : null,
                        r.getId() != null ? r.getId().getEmoji() : null
                )))
        );

        reads.findAll().forEach(r ->
                docs.add(new Document("MessageRead[messageId=%s, userId=%s, readAt=%s]".formatted(
                        r.getMessage() != null ? r.getMessage().getId() : null,
                        r.getUser() != null ? r.getUser().getId() : null,
                        r.getReadAt()
                )))
        );

        notifications.findAll().forEach(n ->
                docs.add(new Document("Notification[id=%s, userId=%s, type=%s]".formatted(
                        n.getId(),
                        n.getUser() != null ? n.getUser().getId() : null,
                        n.getType()
                )))
        );

        devices.findAll().forEach(d ->
                docs.add(new Document("Device[id=%s, userId=%s, type=%s]".formatted(
                        d.getId(),
                        d.getUser() != null ? d.getUser().getId() : null,
                        d.getDeviceType()
                )))
        );

        presences.findAll().forEach(p ->
                docs.add(new Document("Presence[userId=%s, isOnline=%s]".formatted(
                        p.getUser() != null ? p.getUser().getId() : null,
                        p.getIsOnline()
                )))
        );

        otps.findAll().forEach(o ->
                docs.add(new Document("PasswordResetOtp[id=%s, userId=%s]".formatted(
                        o.getId(),
                        o.getUser() != null ? o.getUser().getId() : null
                )))
        );

        tokens.findAll().forEach(t ->
                docs.add(new Document("RefreshToken[id=%s, userId=%s]".formatted(
                        t.getId(),
                        t.getUser() != null ? t.getUser().getId() : null
                )))
        );

        vectorStore.add(docs);
        System.out.println("✅ Vector store loaded with backend metadata (" + docs.size() + " docs)");
    }
}
