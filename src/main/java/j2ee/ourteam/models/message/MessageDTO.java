package j2ee.ourteam.models.message;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import j2ee.ourteam.enums.message.MessageTypeEnum;
import j2ee.ourteam.models.attachment.AttachmentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
  private UUID id;
  private String content;
  private MessageTypeEnum type;
  private LocalDateTime createdAt;
  private LocalDateTime editedAt;
  private Boolean isDeleted;
  private UUID senderId;
  private UUID conversationId;
  private MessageDTO replyTo;

  private Set<AttachmentDTO> attachments = new HashSet<>();;
}