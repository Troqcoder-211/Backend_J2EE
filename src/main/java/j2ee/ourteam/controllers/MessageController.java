package j2ee.ourteam.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.models.message.MessageDTO;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("messages")
public class MessageController {

  @PostMapping
  public ResponseEntity<MessageDTO> sendMessage() {
    return ResponseEntity.ok(null);
  }
}
