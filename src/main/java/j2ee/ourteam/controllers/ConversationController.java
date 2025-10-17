package j2ee.ourteam.controllers;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import j2ee.ourteam.services.conversation.IConversationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
//@AllArgsConstructor
public class ConversationController {
    private final IConversationService _conversationService;

    public ConversationController(IConversationService conversationService){
        _conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<List<ConversationDTO>> getAll(Authentication authentication){
        List<ConversationDTO> conversationDTOList = _conversationService.findAll();
        return ResponseEntity.ok(conversationDTOList);
    }

    @GetMapping("/findbyid/{uuid}")
    public ResponseEntity<ConversationDTO> getById(@PathVariable UUID uuid, Authentication authentication){
        Optional<ConversationDTO> conversationDTO = _conversationService.findById(uuid);
        return conversationDTO.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConversationDTO> createConversation(@RequestBody CreateConversationDTO dto, Authentication authentication){
        ConversationDTO created = _conversationService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PatchMapping(path = "/update/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConversationDTO> updateConversation(@PathVariable UUID uuid, @RequestBody UpdateConversationDTO dto, Authentication authentication){
        ConversationDTO updated = _conversationService.update(uuid, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID uuid, Authentication authentication){
        _conversationService.deleteById(uuid);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/archived/{uuid}")
    public Boolean isArchived(@PathVariable UUID uuid, @RequestBody ArchivedConversationDTO dto, Authentication authentication){
        boolean Archived = _conversationService.isArchived(uuid, dto);

        return Archived;
    }
}
