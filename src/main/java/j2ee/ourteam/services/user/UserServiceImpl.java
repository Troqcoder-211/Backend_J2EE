package j2ee.ourteam.services.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.UserMapper;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.UpdateUserProfileDTO;
import j2ee.ourteam.models.user.UserProfileResponseDTO;
import j2ee.ourteam.models.user.UserResponseDTO;
import j2ee.ourteam.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

  private final UserRepository userRepository;

  private final UserMapper userMapper;

  @Override
  public PageResponse<UserResponseDTO> getUserList(GetUserListRequestDTO request) {
    int page = (request.getPage() >= 0) ? request.getPage() - 1 : 0;
    int limit = (request.getLimit() > 0) ? request.getLimit() : 10;

    Pageable pageable = PageRequest.of(page, limit);

    Page<User> users = (request.getUserName() != null && !request.getUserName().isBlank())
        ? userRepository.findByUserNameContainingIgnoreCase(request.getUserName(), pageable)
        : userRepository.findAll(pageable);

    Page<UserResponseDTO> dtoPage = users.map(userMapper::toUserResponseDTO);

    return PageResponse.from(dtoPage);
  }

  @Override
  public UserProfileResponseDTO getUserProfile(UUID id) {
    return userRepository.findById(id).map(userMapper::toUserProfileResponseDTO).orElse(null);
  }

  @Override
  public UserProfileResponseDTO updateMyProfile(UUID currentUserId, UpdateUserProfileDTO updateDTO) {
    User user = userRepository.findById(currentUserId).orElse(null);
    if (updateDTO.getEmail() != null && !updateDTO.getEmail().isBlank()) {
      user.setEmail(updateDTO.getEmail());
    }

    if (updateDTO.getDisplayName() != null && !updateDTO.getDisplayName().isBlank()) {
      user.setDisplayName(updateDTO.getDisplayName());
    }

    if (updateDTO.getAvatarS3Key() != null && !updateDTO.getAvatarS3Key().isBlank()) {
      user.setAvatarS3Key(updateDTO.getAvatarS3Key());
    }
    userRepository.save(user);
    return userMapper.toUserProfileResponseDTO(user);
  }

  @Override
  public void disableUser(UUID currentUserId) {
    User user = userRepository.findById(currentUserId)
        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

    // Nếu đã bị vô hiệu hóa rồi thì không cần làm lại
    if (Boolean.TRUE.equals(user.getIsDisabled())) {
      throw new IllegalStateException("Tài khoản đã bị vô hiệu hóa");
    }

    user.setIsDisabled(true);
    userRepository.save(user);
  }

  @Override
  public void enableUser(UUID currentUserId) {
    User user = userRepository.findById(currentUserId)
        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

    // Nếu tài khoản đang hoạt động thì không cần bật lại
    if (Boolean.FALSE.equals(user.getIsDisabled())) {
      throw new IllegalStateException("Tài khoản đang hoạt động, không cần kích hoạt lại");
    }

    user.setIsDisabled(false);
    userRepository.save(user);
  }

  @Override
  public void deleteUser(UUID currentUserId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
  }

  @Override
  public List<Object> findAll() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findAll'");
  }

  @Override
  public Optional<Object> findById(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'findById'");
  }

  @Override
  public Object create(Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  public Object update(UUID id, Object dto) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'update'");
  }

  @Override
  public void deleteById(UUID id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

}
