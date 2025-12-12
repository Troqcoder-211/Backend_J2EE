package j2ee.ourteam.models.bot;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCPResponse {
  private String text; // câu trả lời chính
  private List<String> suggestedReplies; // gợi ý quick replies
  private Double confidence; // optional
  private Map<String, Object> raw; // raw payload nếu cần
}
