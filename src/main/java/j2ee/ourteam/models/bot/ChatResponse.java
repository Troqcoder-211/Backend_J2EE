package j2ee.ourteam.models.bot;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
  private String reply;
  private List<String> quickReplies;
}
