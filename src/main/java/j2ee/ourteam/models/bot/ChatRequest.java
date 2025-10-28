package j2ee.ourteam.models.bot;

import lombok.Data;

@Data
public class ChatRequest {
  private String userId;
  private String message;
  private String conversationId;
}
