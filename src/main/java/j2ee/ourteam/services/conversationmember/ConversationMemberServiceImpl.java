package j2ee.ourteam.services.conversationmember;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMember.Role;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.ConversationMemberMapper;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation_member.AddConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.models.conversation_member.UpdateMuteDTO;
import j2ee.ourteam.models.conversation_member.UpdateRoleDTO;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
// @AllArgsConstructor
public class ConversationMemberServiceImpl implements IConversationMemberService {

    private final ConversationMemberMapper _conversationMemberMapper;
    private final ConversationRepository _conversationRepository;
    private final UserRepository _userRepository;
    private final ConversationMemberRepository _conversationMemberRepository;

    @Autowired
    public ConversationMemberServiceImpl(ConversationMemberMapper conversationMemberMapper,
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            ConversationMemberRepository conversationMemberRepository) {
        _conversationMemberMapper = conversationMemberMapper;
        _conversationRepository = conversationRepository;
        _userRepository = userRepository;
        _conversationMemberRepository = conversationMemberRepository;
    }

    @Override
    public List<ConversationMemberDTO> findAll() {
        return List.of();
    }

    @Override
    public Optional<ConversationMemberDTO> findById(ConversationMemberId conversationMemberId) {
        return Optional.empty();
    }

    @Override
    public ConversationMemberDTO create(AddConversationMemberDTO dto) {
        return null;
    }

    @Override
    public ConversationMemberDTO update(ConversationMemberId conversationMemberId, AddConversationMemberDTO dto) {
        return null;
    }

    @Override
    public void deleteById(ConversationMemberId conversationMemberId) {

    }

    // 2.31: GET danh sách thành viên
    @Override
    public ResponseDTO<List<ConversationMemberDTO>> getMember(UUID conversationId, User currentUser) {
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        ResponseDTO<Void> check = checkIfMemberOrOwner(conversationId, user.getId());
        if (!check.isSuccess()) {
            return ResponseDTO.error(check.getMessage());
        }

        List<ConversationMember> members = _conversationMemberRepository.findByIdConversationId(conversationId);
        List<ConversationMemberDTO> dtos = members.stream()
                .map(_conversationMemberMapper::toDto)
                .collect(Collectors.toList());
        return ResponseDTO.success("Lấy danh sách thành viên thành công", dtos);
    }

    // 2.32: POST thêm thành viên
    @Transactional
    @Override
    public ResponseDTO<ConversationMemberDTO> addMember(UUID conversationId, AddConversationMemberDTO dto,
            User currentUser) {
        Optional<User> ownerOpt = _userRepository.findById(currentUser.getId());
        if (ownerOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User owner = ownerOpt.get();

        Optional<Conversation> convOpt = _conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        if (!conversation.getCreatedBy().getId().equals(owner.getId())) {
            return ResponseDTO.error("Only owner can add members");
        }

        Optional<User> newUserOpt = _userRepository.findById(dto.getUserId());
        if (newUserOpt.isEmpty()) {
            return ResponseDTO.error("New user not found");
        }
        User newUser = newUserOpt.get();

        ConversationMemberId memberId = new ConversationMemberId(conversationId, dto.getUserId());
        if (_conversationMemberRepository.existsById(memberId)) {
            return ResponseDTO.error("User is already a member");
        }

        ConversationMember member = _conversationMemberMapper.toEntity(dto);
        member.setConversation(conversation);
        member.setUser(newUser);

        member = _conversationMemberRepository.save(member);
        return ResponseDTO.success("Thêm thành viên thành công", _conversationMemberMapper.toDto(member));
    }

    // 2.33: DELETE xóa thành viên
    @Transactional
    @Override
    public ResponseDTO<Void> removeMember(UUID conversationId, UUID userId, User currentUser) {
        Optional<User> ownerOpt = _userRepository.findById(currentUser.getId());
        if (ownerOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User owner = ownerOpt.get();

        Optional<Conversation> convOpt = _conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        if (!conversation.getCreatedBy().getId().equals(owner.getId())) {
            return ResponseDTO.error("Only owner can remove members");
        }

        ConversationMemberId memberId = new ConversationMemberId(conversationId, userId);
        Optional<ConversationMember> memberOpt = _conversationMemberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return ResponseDTO.error("Member not found");
        }

        _conversationMemberRepository.delete(memberOpt.get());
        return ResponseDTO.success("Xóa thành viên thành công", null);
    }

    // 2.34: PATCH thay đổi role
    @Transactional
    @Override
    public ResponseDTO<ConversationMemberDTO> updateRole(UUID conversationId, UUID userId, UpdateRoleDTO dto,
            User currentUser) {
        Optional<User> ownerOpt = _userRepository.findById(currentUser.getId());
        if (ownerOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User owner = ownerOpt.get();

        Optional<Conversation> convOpt = _conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        if (!conversation.getCreatedBy().getId().equals(owner.getId())) {
            return ResponseDTO.error("Only owner can update role");
        }

        ConversationMemberId memberId = new ConversationMemberId(conversationId, userId);
        Optional<ConversationMember> memberOpt = _conversationMemberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return ResponseDTO.error("Member not found");
        }
        ConversationMember member = memberOpt.get();

        _conversationMemberMapper.updateFromRoleDto(dto, member);
        member = _conversationMemberRepository.save(member);
        return ResponseDTO.success("Cập nhật role thành công", _conversationMemberMapper.toDto(member));
    }

    // 2.35: PATCH bật/tắt mute (chỉ self)
    @Transactional
    @Override
    public ResponseDTO<ConversationMemberDTO> updateMute(UUID conversationId, UUID userId, UpdateMuteDTO dto,
            User currentUser) {
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        if (!user.getId().equals(userId)) {
            return ResponseDTO.error("Only you can update your mute status");
        }

        ConversationMemberId memberId = new ConversationMemberId(conversationId, userId);
        Optional<ConversationMember> memberOpt = _conversationMemberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return ResponseDTO.error("Member not found");
        }
        ConversationMember member = memberOpt.get();

        _conversationMemberMapper.updateFromMuteDto(dto, member);
        member = _conversationMemberRepository.save(member);
        return ResponseDTO.success("Cập nhật mute thành công", _conversationMemberMapper.toDto(member));
    }

    // 2.36: POST rời nhóm (self leave)
    @Transactional
    @Override
    public ResponseDTO<Void> leaveConversation(UUID conversationId, User currentUser) {
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        ConversationMemberId memberId = new ConversationMemberId(conversationId, user.getId());
        Optional<ConversationMember> memberOpt = _conversationMemberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return ResponseDTO.error("You are not a member");
        }
        ConversationMember member = memberOpt.get();

        if (member.getRole() == Role.OWNER) {
            List<ConversationMember> otherMembers = _conversationMemberRepository.findByIdConversationId(conversationId)
                    .stream()
                    .filter(m -> !m.getUser().getId().equals(user.getId()))
                    .toList();

            boolean hasOtherAdminOrOwner = otherMembers.stream()
                    .anyMatch(m -> m.getRole() == Role.ADMIN || m.getRole() == Role.OWNER);

            if (!hasOtherAdminOrOwner) {
                return ResponseDTO.error("Owner cannot leave. Transfer ownership first");
            }
        }

        _conversationMemberRepository.delete(member);
        return ResponseDTO.success("Rời nhóm thành công", null);
    }

    // Helper (đã tối ưu, không cần try-catch nữa vì orElseThrow handle)
    private ResponseDTO<Void> checkIfMemberOrOwner(UUID conversationId, UUID userId) {
        Optional<Conversation> convOpt = _conversationRepository.findById(conversationId);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        ConversationMemberId memberId = new ConversationMemberId(conversationId, userId);
        boolean isMember = _conversationMemberRepository.existsById(memberId);
        if (!isMember && !conversation.getCreatedBy().getId().equals(userId)) {
            return ResponseDTO.error("You are not a member or owner");
        }
        return ResponseDTO.success("OK", null);
    }
}