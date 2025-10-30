package j2ee.ourteam.services.conversation;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.ConversationMapper;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements IConversationService {

    private final ConversationMapper _conversationMapper;
    private final ConversationRepository _conversationRepository;
    private final UserRepository _userRepository;
    private final ConversationMemberRepository _conversationMemberRepository;

    public ConversationServiceImpl(ConversationMapper conversationMapper,
            ConversationRepository conversationRepository,
            UserRepository userRepository, ConversationMemberRepository conversationMemberRepository) {
        _conversationMapper = conversationMapper;
        _conversationRepository = conversationRepository;
        _userRepository = userRepository;
        _conversationMemberRepository = conversationMemberRepository;
    }

    // ====================Bỏ=========================
    @Override
    public List<ConversationDTO> findAll() {
        return List.of();
    }

    @Override
    public Optional<ConversationDTO> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public ConversationDTO create(CreateConversationDTO dto) {
        return null;
    }

    @Override
    public ConversationDTO update(UUID uuid, CreateConversationDTO dto) {
        return null;
    }

    @Override
    public void deleteById(UUID uuid) {

    }
    // ====================================================

    // POST /conversations (tạo mới với user)
    @Transactional
    @Override
    public ResponseDTO<ConversationDTO> createConversation(CreateConversationDTO dto, User currentUser) {
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        Conversation conversation = _conversationMapper.toEntity(dto);
        conversation.setCreatedBy(user);

        Conversation saved = _conversationRepository.save(conversation);

        // Auto add owner as member with ROLE OWNER
        ConversationMemberId ownerId = new ConversationMemberId(saved.getId(), user.getId());
        ConversationMember ownerMember = ConversationMember.builder()
                .id(ownerId)
                .conversation(saved)
                .user(user)
                .role(ConversationMember.Role.OWNER)
                .build();
        _conversationMemberRepository.save(ownerMember);

        return ResponseDTO.success("Tạo conversation thành công", _conversationMapper.toDto(saved));
    }

    // PATCH /conversations/{id} (đổi tên/avatar)
    @Transactional
    @Override
    public ResponseDTO<ConversationDTO> updateConversation(UUID id, UpdateConversationDTO dto, User currentUser) {
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        Optional<Conversation> convOpt = _conversationRepository.findById(id);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        if (!conversation.getCreatedBy().getId().equals(user.getId())) {
            return ResponseDTO.error("Only the group owner can rename or change avatar");
        }

        _conversationMapper.updateEntityFromDto(dto, conversation);
        Conversation updated = _conversationRepository.save(conversation);
        return ResponseDTO.success("Cập nhật conversation thành công", _conversationMapper.toDto(updated));
    }

    // DELETE /conversations/{id} (xóa, code cũ dùng deleteById nhưng với user)
    @Transactional
    @Override
    public ResponseDTO<Void> deleteConversationById(UUID id, User currentUser) {
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        Optional<Conversation> convOpt = _conversationRepository.findById(id);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        if (!conversation.getCreatedBy().getId().equals(user.getId())) {
            return ResponseDTO.error("Only the group owner can delete it");
        }

        _conversationRepository.deleteById(id);
        return ResponseDTO.success("Xóa conversation thành công", null);
    }

    // PATCH /conversations/{id}/archive (chuyển vào archived)
    @Transactional
    @Override
    public ResponseDTO<Boolean> isArchived(UUID id, ArchivedConversationDTO dto, User user) {
        Optional<Conversation> convOpt = _conversationRepository.findById(id);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        _conversationMapper.updateArchivedFromDto(dto, conversation);
        conversation.setIsArchived(true);
        Conversation archived = _conversationRepository.save(conversation);
        return ResponseDTO.success("Chuyển vào archived thành công", archived.getIsArchived());
    }

    // GET /conversations (danh sách của user)
    @Override
    public ResponseDTO<List<ConversationDTO>> getAllConversation(User currentUser) {
        String username = currentUser.getUserName();
        if (username == null || username.isEmpty()) {
            return ResponseDTO.error("User not found");
        }

        List<Conversation> conversations = _conversationRepository.findAllByCreatedBy_UserName(username);
        List<ConversationDTO> dtos = conversations.stream()
                .map(_conversationMapper::toDto)
                .collect(Collectors.toList());
        return ResponseDTO.success("Lấy danh sách conversation của user thành công", dtos);
    }

    // GET /conversations/{id}
    @Override
    public ResponseDTO<ConversationDTO> findConversationById(UUID id, User user) {
        Optional<Conversation> convOpt = _conversationRepository.findById(id);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        return ResponseDTO.success("Lấy chi tiết conversation thành công", _conversationMapper.toDto(convOpt.get()));
    }
}