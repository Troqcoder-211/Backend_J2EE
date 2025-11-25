package j2ee.ourteam.services.bot;

import j2ee.ourteam.models.bot.MCPResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface McpAPI {

    MCPResponse chat(List<Map<String, String>> messages);
}
