package j2ee.ourteam.models.conversation_member;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConversationMemberDTO {
   private UUID userId;
   private UUID conversationId;
   private String userName;
   private String displayName;
   private String Role;
   private Boolean isMuted;
   private LocalDateTime joinedAt;
   private UUID lastMessageId;
   private LocalDateTime lastReadAt;

}
