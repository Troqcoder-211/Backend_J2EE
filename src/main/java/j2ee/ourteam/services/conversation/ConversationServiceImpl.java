package j2ee.ourteam.services.conversation;

import j2ee.ourteam.entities.Conversation;
import j2ee.ourteam.entities.ConversationMember;
import j2ee.ourteam.entities.ConversationMemberId;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.ConversationMapper;
import j2ee.ourteam.mapping.ConversationMemberMapper;
import j2ee.ourteam.models.conversation.ResponseDTO;
import j2ee.ourteam.models.conversation.ArchivedConversationDTO;
import j2ee.ourteam.models.conversation.ConversationDTO;
import j2ee.ourteam.models.conversation.CreateConversationDTO;
import j2ee.ourteam.models.conversation.UpdateConversationDTO;
import j2ee.ourteam.models.conversation_member.ConversationMemberDTO;
import j2ee.ourteam.redis.ConversationSoftDeleteService;
import j2ee.ourteam.repositories.ConversationMemberRepository;
import j2ee.ourteam.repositories.ConversationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements IConversationService {

    private final ConversationMapper _conversationMapper;
    private final ConversationMemberMapper _conversationMemberMapper;
    private final ConversationRepository _conversationRepository;
    private final UserRepository _userRepository;
    private final ConversationMemberRepository _conversationMemberRepository;
    private final ConversationSoftDeleteService _conversationSoftDeleteService;

    public ConversationServiceImpl(
            ConversationMapper conversationMapper,
            ConversationMemberMapper conversationMemberMapper,
            ConversationRepository conversationRepository,
            UserRepository userRepository,
            ConversationMemberRepository conversationMemberRepository,
            ConversationSoftDeleteService conversationSoftDeleteService) {
        _conversationMapper = conversationMapper;
        _conversationMemberMapper = conversationMemberMapper;
        _conversationRepository = conversationRepository;
        _userRepository = userRepository;
        _conversationMemberRepository = conversationMemberRepository;
        _conversationSoftDeleteService = conversationSoftDeleteService;
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
        User user = _userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (dto == null) return ResponseDTO.error("Invalid request");

        boolean isDM = dto.getConversationType() != null && "DM".equalsIgnoreCase(dto.getConversationType().toString());

        if (isDM) {
            if (dto.getMembers() == null || dto.getMembers().isEmpty())
                return ResponseDTO.error("DM requires one other member");

            UUID otherUserId = dto.getMembers().get(0).getUserId();
            if (otherUserId == null) return ResponseDTO.error("Invalid member id for DM");
            if (otherUserId.equals(user.getId())) return ResponseDTO.error("Cannot create DM with yourself");

            Optional<Conversation> existedDm = _conversationMemberRepository.findExistingDm(user.getId(), otherUserId);
            if (existedDm.isPresent()) {
                Conversation existing = existedDm.get();
                boolean deletedByUser = _conversationSoftDeleteService.isDeleted(user.getId().toString(), existing.getId().toString());
                if (!deletedByUser) {
                    return ResponseDTO.error("Conversation giữa các thành viên đã tồn tại");
                }

                // restore conversation cho user A
                _conversationSoftDeleteService.restore(user.getId().toString(), existing.getId().toString());

                // reset các thông tin liên quan đến user A (lastReadMessage, joinedAt...)
                existing.getMembers().stream()
                        .filter(m -> m.getUser().getId().equals(user.getId()))
                        .forEach(m -> {
                            m.setLastReadMessageId(null);
                            m.setJoinedAt(LocalDateTime.now());
                        });

                _conversationRepository.save(existing);

                return ResponseDTO.success("Tạo thành công", _conversationMapper.toDto(existing, user, _conversationMemberMapper));
            }

            // Nếu chưa có conversation nào → tạo mới
            Conversation conversation = _conversationMapper.toEntity(dto);
            conversation.setCreatedBy(user);
            Conversation saved = _conversationRepository.save(conversation);

            // Thêm admin (creator)
            ConversationMember adminMember = ConversationMember.builder()
                    .id(new ConversationMemberId(saved.getId(), user.getId()))
                    .conversation(saved)
                    .user(user)
                    .role(ConversationMember.Role.ADMIN)
                    .isMuted(false)
                    .joinedAt(LocalDateTime.now())
                    .build();
            _conversationMemberRepository.save(adminMember);

            // Thêm thành viên còn lại
            User otherUser = _userRepository.findById(otherUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Other user not found"));
            ConversationMember member = ConversationMember.builder()
                    .id(new ConversationMemberId(saved.getId(), otherUser.getId()))
                    .conversation(saved)
                    .user(otherUser)
                    .role(ConversationMember.Role.MEMBER)
                    .isMuted(false)
                    .joinedAt(LocalDateTime.now())
                    .build();
            _conversationMemberRepository.save(member);

            saved.setMembers(_conversationMemberRepository.findByIdConversationId(saved.getId()));
            return ResponseDTO.success("Tạo DM thành công", _conversationMapper.toDto(saved, user, _conversationMemberMapper));
        }

        // ===================== GROUP case =====================
        // Gom danh sách member
        Set<UUID> memberIdSet = new LinkedHashSet<>();
        memberIdSet.add(user.getId());
        if (dto.getMembers() != null) {
            for (ConversationMemberDTO m : dto.getMembers()) {
                UUID uid = m.getUserId();
                if (uid != null && !uid.equals(user.getId())) memberIdSet.add(uid);
            }
        }

        List<UUID> memberIds = new ArrayList<>(memberIdSet);
        long size = memberIds.size();

        // Kiểm tra conversation group đã tồn tại
        if (!memberIds.isEmpty()) {
            List<Conversation> existed = _conversationMemberRepository.findConversationWithExactMembers(memberIds, size);

            for (Conversation c : existed) {
                // Nếu soft-deleted với user A → restore
                if (_conversationSoftDeleteService.isDeleted(user.getId().toString(), c.getId().toString())) {
                    _conversationSoftDeleteService.restore(user.getId().toString(), c.getId().toString());

                    // reset trạng thái member cho user A
                    c.getMembers().stream()
                            .filter(m -> m.getUser().getId().equals(user.getId()))
                            .forEach(m -> {
                                m.setLastReadMessageId(null);
                                m.setJoinedAt(LocalDateTime.now());
                            });

                    _conversationRepository.save(c);
                    return ResponseDTO.success("Tạo thành công", _conversationMapper.toDto(c, user, _conversationMemberMapper));
                } else {
                    return ResponseDTO.error("Conversation giữa các thành viên đã tồn tại");
                }
            }
        }

        // Nếu chưa có conversation nào → tạo mới
        Conversation conversation = _conversationMapper.toEntity(dto);
        conversation.setCreatedBy(user);
        Conversation saved = _conversationRepository.save(conversation);

        // Thêm admin
        ConversationMember adminMember = ConversationMember.builder()
                .id(new ConversationMemberId(saved.getId(), user.getId()))
                .conversation(saved)
                .user(user)
                .role(ConversationMember.Role.ADMIN)
                .isMuted(false)
                .joinedAt(LocalDateTime.now())
                .build();
        _conversationMemberRepository.save(adminMember);

        // Thêm các member khác
        if (dto.getMembers() != null) {
            for (ConversationMemberDTO m : dto.getMembers()) {
                UUID uid = m.getUserId();
                if (uid == null || uid.equals(user.getId())) continue;
                User newUser = _userRepository.findById(uid).orElse(null);
                if (newUser == null) continue;

                ConversationMember member = ConversationMember.builder()
                        .id(new ConversationMemberId(saved.getId(), newUser.getId()))
                        .conversation(saved)
                        .user(newUser)
                        .role(ConversationMember.Role.MEMBER)
                        .isMuted(false)
                        .joinedAt(LocalDateTime.now())
                        .build();
                _conversationMemberRepository.save(member);
            }
        }

        saved.setMembers(_conversationMemberRepository.findByIdConversationId(saved.getId()));
        return ResponseDTO.success("Tạo group conversation thành công", _conversationMapper.toDto(saved, user, _conversationMemberMapper));
    }



    //Update
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

        if (_conversationSoftDeleteService.isDeleted(user.getId().toString(), id.toString())) {
            return ResponseDTO.error("You cannot update a deleted conversation");
        }

        boolean updated = false;

        // ✅ Nếu FE chỉ gửi name thì chỉ cập nhật name
        if (dto.getName() != null && !dto.getName().isBlank()) {
            conversation.setName(dto.getName());
            updated = true;
        }

        // ✅ Nếu FE chỉ gửi avatarS3Key thì chỉ cập nhật avatar
        if (dto.getAvatarS3Key() != null && !dto.getAvatarS3Key().isBlank()) {
            conversation.setAvatarS3Key(dto.getAvatarS3Key());
            updated = true;
        }

        if (!updated) {
            return ResponseDTO.error("Không có dữ liệu nào để cập nhật");
        }

        Conversation saved = _conversationRepository.save(conversation);
        // return mapped with current user + member mapper
        return ResponseDTO.success(
                "Cập nhật conversation thành công",
                _conversationMapper.toDto(saved, user, _conversationMemberMapper)
        );
    }

    // DELETE /conversations/{id} (xóa, code cũ dùng deleteById nhưng với user)
    @Transactional
    @Override
    public ResponseDTO<Void> deleteConversationById(UUID id, User currentUser) {

        // Check user tồn tại
        Optional<User> userOpt = _userRepository.findById(currentUser.getId());
        if (userOpt.isEmpty()) {
            return ResponseDTO.error("User not found");
        }
        User user = userOpt.get();

        // Check conversation tồn tại
        Optional<Conversation> convOpt = _conversationRepository.findById(id);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conversation = convOpt.get();

        // Check user có trong conversation hay không
        boolean isMember = _conversationMemberRepository.findByConversationIdAndUserId(id, user.getId()).isPresent();

        if (!isMember) {
            return ResponseDTO.error("You are not a member of this conversation");
        }

        // XÓA MỀM THEO USER (Redis)
        _conversationSoftDeleteService.markDeleted(user.getId().toString(), id.toString());

        return ResponseDTO.success("Đã xóa đoạn hội thoại khỏi tài khoản của bạn", null);
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

        // Nếu DTO có isArchived, dùng giá trị đó, ngược lại giữ nguyên
        if (dto.getIsArchived() != null) {
            conversation.setIsArchived(dto.getIsArchived());
        }

        conversation.setIsArchived(dto.getIsArchived());
        Conversation archived = _conversationRepository.save(conversation);
        return ResponseDTO.success("Thao tác thành công", archived.getIsArchived());
    }

    // GET /conversations (danh sách của user)
    @Override
    public ResponseDTO<List<ConversationDTO>> getAllConversation(User currentUser) {
        String username = currentUser.getUserName();
        if (username == null || username.isEmpty()) {
            return ResponseDTO.error("User not found");
        }

        List<Conversation> conversations = _conversationRepository.findAllByMemberId(currentUser.getId());

        // Lọc các conversation đã bị xóa (soft delete) với user hiện tại
        List<Conversation> filtered = conversations.stream()
                .filter(c -> !_conversationSoftDeleteService.isDeleted(currentUser.getId().toString(), c.getId().toString()))
                .collect(Collectors.toList());

        // Map bằng overload toDto(conversation, currentUser, memberMapper)
        List<ConversationDTO> dtos = filtered.stream()
                .map(c -> _conversationMapper.toDto(c, currentUser, _conversationMemberMapper))
                .collect(Collectors.toList());
        return ResponseDTO.success("Lấy danh sách conversation của user thành công", dtos);
    }

    // GET /conversations/{id}
    @Override
    public ResponseDTO<ConversationDTO> findConversationById(UUID id, User user) {
        // Check soft delete trước
        if (_conversationSoftDeleteService.isDeleted(user.getId().toString(), id.toString())) {
            return ResponseDTO.error("Conversation not found"); // Treat as hidden
        }

        Optional<Conversation> convOpt = _conversation_repository_find_helper(id);
        if (convOpt.isEmpty()) {
            return ResponseDTO.error("Conversation not found");
        }
        Conversation conv = convOpt.get();
        return ResponseDTO.success("Lấy chi tiết conversation thành công", _conversationMapper.toDto(conv, user, _conversationMemberMapper));
    }

    // tiny helper to keep main code readable
    private Optional<Conversation> _conversation_repository_find_helper(UUID id) {
        return _conversationRepository.findById(id);
    }
}
