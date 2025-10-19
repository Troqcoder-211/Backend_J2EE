package j2ee.ourteam.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.models.bot.ChatRequest;
import j2ee.ourteam.models.bot.ChatResponse;
import j2ee.ourteam.services.bot.ChatbotService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {
  private final ChatbotService chatbotService;

  @PostMapping
  public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
    ChatResponse response = chatbotService.handleMessage(request);
    return ResponseEntity.ok(response);
  }
}
