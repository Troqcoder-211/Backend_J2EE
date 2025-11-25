package j2ee.ourteam.services.user;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.UpdateUserProfileDTO;
import j2ee.ourteam.models.user.UserProfileResponseDTO;
import j2ee.ourteam.models.user.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest extends BaseTest {

    private UserServiceImpl userService;

    private User user;

    // fixed UUIDs to avoid EntityNotFoundException
    private final UUID FIXED_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // manually create service with mocks
        userService = new UserServiceImpl(userRepository, s3Service, userMapper);

        // prepare sample user
        user = mockUser();
        user.setId(FIXED_USER_ID);
    }

    @Test
    void getUserList_ShouldReturnPagedResponse() {
        GetUserListRequestDTO request = new GetUserListRequestDTO();
        request.setPage(1);
        request.setLimit(10);

        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.toUserResponseDTO(user)).thenReturn(new UserResponseDTO());

        PageResponse<UserResponseDTO> result = userService.getUserList(request);

        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getUserProfile_ShouldReturnDTO_WhenUserExists() {
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponseDTO(user)).thenReturn(new UserProfileResponseDTO());

        UserProfileResponseDTO dto = userService.getUserProfile(FIXED_USER_ID);
        assertThat(dto).isNotNull();
    }

    @Test
    void getUserProfile_ShouldReturnNull_WhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserProfileResponseDTO dto = userService.getUserProfile(id);
        assertThat(dto).isNull();
    }

    @Test
    void updateMyProfile_ShouldUpdateFields() {
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponseDTO(user)).thenReturn(new UserProfileResponseDTO());

        UpdateUserProfileDTO updateDTO = new UpdateUserProfileDTO();
        updateDTO.setEmail("newemail@example.com");
        updateDTO.setDisplayName("New Name");
        updateDTO.setAvatarS3Key("new-avatar-key");

        UserProfileResponseDTO result = userService.updateMyProfile(FIXED_USER_ID, updateDTO);

        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
        assertThat(user.getDisplayName()).isEqualTo("New Name");
        assertThat(user.getAvatarS3Key()).isEqualTo("new-avatar-key");
        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateMyProfile_ShouldThrow_WhenUserNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateMyProfile(id, new UpdateUserProfileDTO()));
    }

    @Test
    void updateAvatar_ShouldUploadAndSetNewAvatar() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));
        when(s3Service.uploadFile(file)).thenReturn("new-url");

        String result = userService.updateAvatar(FIXED_USER_ID, file);

        assertThat(result).isEqualTo("new-url");
        assertThat(user.getAvatarS3Key()).isEqualTo("new-url");
        verify(s3Service, times(1)).uploadFile(file);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateAvatar_ShouldDeleteOldAvatar() throws IOException {
        user.setAvatarS3Key("old-key");
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));
        when(s3Service.uploadFile(file)).thenReturn("new-url");

        userService.updateAvatar(FIXED_USER_ID, file);

        verify(s3Service, times(1)).deleteFile("old-key");
        verify(s3Service, times(1)).uploadFile(file);
    }

    @Test
    void disableUser_ShouldSetIsDisabledTrue() {
        user.setIsDisabled(false);
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));

        userService.disableUser(FIXED_USER_ID);

        assertThat(user.getIsDisabled()).isTrue();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void disableUser_ShouldThrow_WhenAlreadyDisabled() {
        user.setIsDisabled(true);
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> userService.disableUser(FIXED_USER_ID));
    }

    @Test
    void enableUser_ShouldSetIsDisabledFalse() {
        user.setIsDisabled(true);
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));

        userService.enableUser(FIXED_USER_ID);

        assertThat(user.getIsDisabled()).isFalse();
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void enableUser_ShouldThrow_WhenAlreadyEnabled() {
        user.setIsDisabled(false);
        when(userRepository.findById(FIXED_USER_ID)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> userService.enableUser(FIXED_USER_ID));
    }
}
