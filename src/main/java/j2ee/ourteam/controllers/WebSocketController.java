package j2ee.ourteam.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.notification.NotificationDTO;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
  private final SimpMessagingTemplate messagingTemplate;

  // Client gửi tin nhắn tới server
  @MessageMapping("/chat.sendMessage")
  public void sendMessage(@Payload MessageDTO message) {
    messagingTemplate.convertAndSend("/topic/conversations/" + message.getConversationId(), message);
  }

  // Gửi notification realtime
  @MessageMapping("/notification.send")
  public void sendNotification(@Payload NotificationDTO notification) {
    messagingTemplate.convertAndSend("/queue/notifications/" + notification.getUserId(), notification);
  }

  // có thể gọi trong service để push noti sau khi lưu DB
  public void pushNotification(NotificationDTO dto) {
    messagingTemplate.convertAndSend("/queue/notifications/" + dto.getUserId(), dto);
  }

  public void pushMessage(MessageDTO dto) {
    messagingTemplate.convertAndSend("/topic/notications/" + dto.getConversationId(), dto);
  }
}
