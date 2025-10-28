package j2ee.ourteam.services.bot;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import j2ee.ourteam.models.bot.MCPRequest;
import j2ee.ourteam.models.bot.MCPResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MCPClient {

  private final RestTemplate restTemplate = new RestTemplate();
  private final String MCP_ENDPOINT = "http://localhost:8081/mcp/chat"; // MCP server

  public MCPResponse send(MCPRequest request) {
    return restTemplate.postForObject(MCP_ENDPOINT, request, MCPResponse.class);
  }
}
