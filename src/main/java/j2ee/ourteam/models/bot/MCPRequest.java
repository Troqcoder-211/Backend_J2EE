package j2ee.ourteam.models.bot;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MCPRequest {
  private String userId;
  private String conversationId;
  private String input; // câu hỏi/ngõ vào
  private Map<String, Object> context; // optional: context thêm
  private Map<String, String> metadata; // optional: headers / thông tin thêm
}
