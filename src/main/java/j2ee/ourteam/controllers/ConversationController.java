package j2ee.ourteam.controllers;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import j2ee.ourteam.models.conversation_member.AddConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation_member.UpdateMuteDTO;
import j2ee.ourteam.models.conversation_member.UpdateRoleDTO;
import j2ee.ourteam.services.conversation.IConversationService;
import j2ee.ourteam.services.conversationmember.IConversationMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
public class ConversationController {
    private final IConversationService _conversationService;
    private final IConversationMemberService _conversationMemberService;
    @Autowired

    public ConversationController(IConversationService conversationService,
            IConversationMemberService conversationMemberService) {
        _conversationService = conversationService;
        _conversationMemberService = conversationMemberService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<ConversationDTO>>> getAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<List<ConversationDTO>> response = _conversationService.getAllConversation(user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/findbyid/{uuid}")
    public ResponseEntity<ResponseDTO<ConversationDTO>> getById(@PathVariable UUID uuid,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<ConversationDTO> response = _conversationService.findConversationById(uuid, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<ConversationDTO>> createConversation(@RequestBody CreateConversationDTO dto, Authentication authentication){
        User user =(User) authentication.getPrincipal();

        ResponseDTO<ConversationDTO> response = _conversationService.createConversation(dto, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping(path = "/update/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDTO<ConversationDTO>> updateConversation(@PathVariable UUID uuid, @RequestBody UpdateConversationDTO dto, Authentication authentication){
        User user = (User) authentication.getPrincipal();

        ResponseDTO<ConversationDTO> response = _conversationService.updateConversation(uuid, dto, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("/delete/{uuid}")
    public ResponseEntity<ResponseDTO<Void>> deleteConversation(@PathVariable UUID uuid,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ResponseDTO<Void> response =_conversationService.deleteConversationById(uuid, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping("/archived/{uuid}")
    public ResponseEntity<ResponseDTO<Boolean>> isArchived(@PathVariable UUID uuid,
            @RequestBody ArchivedConversationDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<Boolean> response = _conversationService.isArchived(uuid, dto, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping(path = "/{uuid}/members")
    public ResponseEntity<ResponseDTO<List<ConversationMemberDTO>>> getAllMembers(@PathVariable("uuid") UUID uuid,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<List<ConversationMemberDTO>> response = _conversationMemberService.getMember(uuid, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping(path = "/{uuid}/addMember")
    public ResponseEntity<ResponseDTO<ConversationMemberDTO>> addMember(@PathVariable UUID uuid,
            @Valid @RequestBody AddConversationMemberDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<ConversationMemberDTO> response = _conversationMemberService.addMember(uuid, dto, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping("/{uuid}/role/{userId}")
    public ResponseEntity<ResponseDTO<ConversationMemberDTO>> updateRole(@PathVariable UUID uuid,
            @PathVariable UUID userId, @Valid @RequestBody UpdateRoleDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<ConversationMemberDTO> response = _conversationMemberService.updateRole(uuid, userId, dto, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PatchMapping("/{uuid}/muted/{userId}")
    public ResponseEntity<ResponseDTO<ConversationMemberDTO>> updateMute(@PathVariable UUID uuid,
            @PathVariable UUID userId, @Valid @RequestBody UpdateMuteDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<ConversationMemberDTO> response = _conversationMemberService.updateMute(uuid, userId, dto, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @DeleteMapping("{uuid}/remove/{userId}")
    public ResponseEntity<ResponseDTO<Void>> removeMember(@PathVariable UUID uuid, @PathVariable UUID userId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<Void> response = _conversationMemberService.removeMember(uuid, userId, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{uuid}/leave")
    public ResponseEntity<ResponseDTO<Void>> leaveConversation(@PathVariable UUID uuid, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ResponseDTO<Void> response = _conversationMemberService.leaveConversation(uuid, user);
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
