package j2ee.ourteam.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import j2ee.ourteam.models.messagereaction.MessageReactionDTO;
import j2ee.ourteam.models.page.PageFilter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.models.message.CreateMessageDTO;
import j2ee.ourteam.models.message.MessageDTO;
import j2ee.ourteam.models.message.MessageFilter;
import j2ee.ourteam.models.message.UpdateMessageDTO;
import j2ee.ourteam.models.messagereaction.CreateMessageReactionDTO;
import j2ee.ourteam.models.messageread.MessageReadDTO;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.models.message.CreateReplyMessageDTO;
import j2ee.ourteam.services.message.IMessageService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

  @PatchMapping("/{id}")
  public ResponseEntity<MessageDTO> editMessage(@PathVariable UUID id,
      @RequestBody @Valid UpdateMessageDTO messageDTO) {
    return ResponseEntity.ok(messageService.update(id, messageDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageDTO> deleteSoftMessage(@PathVariable UUID id) {
    return ResponseEntity.ok(messageService.softDelete(id));
  }

  @GetMapping
  public ResponseEntity<PageResponse<MessageDTO>> getMessages(@ModelAttribute MessageFilter filter) {
    Page<MessageDTO> page = messageService.findAllPaged(filter);
    return ResponseEntity.ok(PageResponse.from(page));
  }

  @GetMapping("/{id}/reactions")
  public ResponseEntity<PageResponse<MessageReactionDTO>> getReactions(@PathVariable UUID id,
      @ModelAttribute @Valid PageFilter pageFilter) {
    Page<MessageReactionDTO> page = messageService.getReactions(id, pageFilter.getPage(), pageFilter.getLimit());

    return ResponseEntity.ok(PageResponse.from(page));
  }

  @PostMapping("/{id}/reactions")
  public ResponseEntity<Map<String, Object>> reactionMessage(@PathVariable UUID id,
      @RequestBody @Valid CreateMessageReactionDTO messageReactionDTO) {
    messageService.addReaction(id, messageReactionDTO);

    Map<String, Object> response = new HashMap<>();

    response.put("status", "success");
    response.put("message", "Message is reacted");

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}/reactions/{emoji}")
  public ResponseEntity<Map<String, Object>> deleteReaction(
      @PathVariable UUID id,
      @PathVariable String emoji,
      @RequestParam UUID userId) {
    messageService.deleteReaction(id, userId, emoji);
    Map<String, Object> response = new HashMap<>();

    response.put("status", "success");
    response.put("message", "Reaction is deleted");

    return ResponseEntity.ok(response);
  }

  @PostMapping("/conversation/{conversationId}/mark-as-read")
  public ResponseEntity<PageResponse<MessageReadDTO>> markConversationAsRead(
      @PathVariable UUID conversationId,
      @RequestParam UUID userId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("readAt").descending());
    Page<MessageReadDTO> result = messageService.markConversationAsRead(conversationId, userId, pageable);
    return ResponseEntity.ok(PageResponse.from(result));
  }

  @GetMapping("/{messageId}/readers")
  public ResponseEntity<PageResponse<MessageReadDTO>> getMessageReaders(
      @PathVariable UUID messageId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int limit) {

    Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("readAt").descending());
    Page<MessageReadDTO> result = messageService.getMessageReaders(messageId, pageable);
    return ResponseEntity.ok(PageResponse.from(result));
  }

  @PostMapping("/reply")
  public ResponseEntity<MessageDTO> replyToMessage(@RequestBody @Valid CreateReplyMessageDTO dto) {
    MessageDTO saved = messageService.reply(dto);
    return ResponseEntity.ok(saved);
  }
}
