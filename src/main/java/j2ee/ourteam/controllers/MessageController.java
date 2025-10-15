package j2ee.ourteam.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.DeleteMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.UpdateMessageDTO;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.services.message.IMessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("messages")
@AllArgsConstructor
public class MessageController {
  @Autowired
  private final IMessageService messageService;

  @PostMapping
  public ResponseEntity<MessageDTO> sendMessage(@RequestBody @Valid CreateMessageDTO messageDTO) {
    return ResponseEntity.ok(messageService.create(messageDTO));
  }

  @GetMapping("/{conversationId}")
  public String getListMessages(@PathVariable String conversationId) {
    return new String();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<MessageDTO> editMessage(@PathVariable UUID id,
      @RequestBody @Valid UpdateMessageDTO messageDTO) {
    return ResponseEntity.ok(messageService.update(id, messageDTO));
  }

  @DeleteMapping("/{id}")
  public String deleteMessage(@PathVariable UUID id) {
    return new String();
  }

  @PostMapping("/{id} / reactions")
  public String reactionMessage(@PathVariable UUID id,
      @RequestBody @Valid CreateMessageReactionDTO messageReactionDTO) {

    return new String();
  }

  @DeleteMapping("/{id} / reactions/{emoji}")
  public String deleteReaction(@PathVariable UUID id, @PathVariable String emoji) {
    return new String();
  }

  @PostMapping("/{id}/read")
  public String markAsRead(@PathVariable UUID id) {
    // Đánh dấu bản ghi có id là đã đọc
    return "Marked as read";
  }

  @GetMapping("/{id}/reads")
  public String getReadStatus(@PathVariable UUID id) {
    // Lấy trạng thái đã đọc hoặc danh sách người đã đọc
    return "Read status";
  }

}
