package j2ee.ourteam.services.user;

import j2ee.ourteam.services.aws.S3Service;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.UserMapper;
import j2ee.ourteam.models.page.PageResponse;
import j2ee.ourteam.models.user.*;
import j2ee.ourteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === getUserProfile ===
    @Test
    void testGetUserProfile_Found() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        UserProfileResponseDTO dto = new UserProfileResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponseDTO(user)).thenReturn(dto);

        UserProfileResponseDTO result = userService.getUserProfile(userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserProfile_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertNull(userService.getUserProfile(userId));
    }

    // === updateMyProfile ===
    @Test
    void testUpdateMyProfile_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setDisplayName("New Name");
        dto.setEmail("newemail@test.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserProfileResponseDTO(user)).thenReturn(new UserProfileResponseDTO());

        UserProfileResponseDTO result = userService.updateMyProfile(userId, dto);

        assertNotNull(result);
        assertEquals("New Name", user.getDisplayName());
        assertEquals("newemail@test.com", user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateMyProfile_UserNotFound() {
        UUID userId = UUID.randomUUID();
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateMyProfile(userId, dto));
    }

    // === updateAvatar ===
    @Test
    void testUpdateAvatar_Success() throws IOException {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setAvatarS3Key("old-key");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Service.uploadFile(multipartFile)).thenReturn("new-key");

        String result = userService.updateAvatar(userId, multipartFile);

        assertEquals("new-key", result);
        verify(s3Service).deleteFile("old-key");
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateAvatar_UserNotFound() throws IOException {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateAvatar(userId, multipartFile));
    }

    // === disableUser ===
    @Test
    void testDisableUser_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setIsDisabled(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.disableUser(userId);

        assertTrue(user.getIsDisabled());
        verify(userRepository).save(user);
    }

    @Test
    void testDisableUser_AlreadyDisabled() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setIsDisabled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> userService.disableUser(userId));
    }

    // === enableUser ===
    @Test
    void testEnableUser_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setIsDisabled(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.enableUser(userId);

        assertFalse(user.getIsDisabled());
        verify(userRepository).save(user);
    }

    @Test
    void testEnableUser_AlreadyEnabled() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setIsDisabled(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> userService.enableUser(userId));
    }

    // === getUserList ===
    @Test
    void testGetUserList_NoFilter() {
        GetUserListRequestDTO request = new GetUserListRequestDTO();
        request.setPage(1);
        request.setLimit(10);

        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        PageResponse<UserResponseDTO> result = userService.getUserList(request);

        assertNotNull(result);
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetUserList_WithFilter() {
        GetUserListRequestDTO request = new GetUserListRequestDTO();
        request.setPage(1);
        request.setLimit(10);
        request.setUserName("test");

        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findByUserNameContainingAndIsDisabledFalse(eq("test"), any(Pageable.class))).thenReturn(page);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        PageResponse<UserResponseDTO> result = userService.getUserList(request);

        assertNotNull(result);
        verify(userRepository).findByUserNameContainingAndIsDisabledFalse(eq("test"), any(Pageable.class));
    }

    // === Các hàm chưa triển khai: deleteUser, findAll, findById, create, update, deleteById ===
    @Test
    void testDeleteUser_Unimplemented() {
        UUID userId = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void testFindAll_Unimplemented() {
        assertThrows(UnsupportedOperationException.class, () -> userService.findAll());
    }

    @Test
    void testFindById_Unimplemented() {
        assertThrows(UnsupportedOperationException.class, () -> userService.findById(UUID.randomUUID()));
    }

    @Test
    void testCreate_Unimplemented() {
        assertThrows(UnsupportedOperationException.class, () -> userService.create(new Object()));
    }

    @Test
    void testUpdate_Unimplemented() {
        assertThrows(UnsupportedOperationException.class, () -> userService.update(UUID.randomUUID(), new Object()));
    }

    @Test
    void testDeleteById_Unimplemented() {
        assertThrows(UnsupportedOperationException.class, () -> userService.deleteById(UUID.randomUUID()));
    }
}
