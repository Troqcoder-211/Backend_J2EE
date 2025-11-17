package j2ee.ourteam.services.auth;

import j2ee.ourteam.entities.*;
import j2ee.ourteam.models.auth.*;
import j2ee.ourteam.repositories.*;
import j2ee.ourteam.services.mail.IMailService;
import j2ee.ourteam.services.otp.IOtpService;

import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PresenceRepository presenceRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private IOtpService otpService;

    @Mock
    private IMailService mailService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ==================== LOGIN ====================
    @Test
    void login_success() {
        // --- arrange
        LoginRequestDTO request = new LoginRequestDTO();
        request.setUserName("john");
        request.setPassword("12345678");
        request.setDeviceType("android");
        request.setPushToken("token123");

        User fakeUser = User.builder()
                .id(UUID.randomUUID())
                .userName("john")
                .password("encoded_pass")
                .build();

        // the id we want save(...) to set on the passed device
        UUID assignedDeviceId = UUID.randomUUID();

        // when user lookup
        when(userRepository.findByUserName("john")).thenReturn(Optional.of(fakeUser));

        // simulate JPA save: set id on the passed device instance and return it
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
            Device arg = inv.getArgument(0);
            // set id on the passed device (simulate JPA assigning id)
            arg.setId(assignedDeviceId);
            return arg;
        });

        // jwtService should accept any user and any UUID (since device id may be set dynamically)
        when(jwtService.generateAccessToken(any(User.class), any(UUID.class))).thenReturn("access_123");
        when(jwtService.generateRefreshToken(any(User.class), any(UUID.class))).thenReturn("refresh_123");

        // when saving refresh token repo, return the same entity (or any)
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        // mock authentication manager authenticate to return an Authentication (no exception)
        when(authenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        // --- act
        LoginResponseDTO response = authService.login(request, mock(HttpServletResponse.class));

        // --- assert
        assertNotNull(response, "response should not be null");
        assertEquals("access_123", response.getAccessToken(), "access token mismatch");
        assertEquals("refresh_123", response.getRefreshToken(), "refresh token mismatch");
        assertEquals(assignedDeviceId, response.getDeviceId(), "device id mismatch");

        // verify interactions
        verify(authenticationManager).authenticate(any());
        verify(deviceRepository).save(any(Device.class));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(jwtService).generateAccessToken(any(User.class), any(UUID.class));
        verify(jwtService).generateRefreshToken(any(User.class), any(UUID.class));
    }



    // ==================== REFRESH ACCESS TOKEN ====================
    @Test
    void refreshAccessToken_success() {
        UUID deviceId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(jwtService.extractDeviceId("refreshToken")).thenReturn(deviceId);
        when(refreshTokenRepository.findByToken("refreshToken")).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtService.generateAccessToken(user, deviceId)).thenReturn("new_access");

        String token = authService.refreshAccessToken("refreshToken");
        assertEquals("new_access", token);
    }

    @Test
    void refreshAccessToken_tokenExpired_throws() {
        UUID deviceId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).build();
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(jwtService.extractDeviceId("refreshToken")).thenReturn(deviceId);
        when(refreshTokenRepository.findByToken("refreshToken")).thenReturn(Optional.of(refreshTokenEntity));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.refreshAccessToken("refreshToken"));
        assertEquals("Refresh token đã hết hạn", ex.getMessage());
    }

    // ==================== REGISTER ====================
    @Test
    void register_success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUserName("alice");
        dto.setPassword("password123");
        dto.setDisplayName("Alice");
        dto.setEmail("alice@example.com");

        when(userRepository.findByUserName("alice")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pass");

        authService.register(dto);

        verify(userRepository).save(any(User.class));
        verify(presenceRepository).save(any(Presence.class));
    }

    @Test
    void register_existingUser_throws() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUserName("alice");
        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(new User()));

        assertThrows(EntityExistsException.class, () -> authService.register(dto));
    }

    @Test
    void register_shortPassword_throws() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUserName("bob");
        dto.setPassword("short");

        assertThrows(RuntimeException.class, () -> authService.register(dto));
    }

    // ==================== LOGOUT ====================
    @Test
    void logout_success() {
        String refreshToken = "token";
        UUID deviceId = UUID.randomUUID();
        User user = User.builder().userName("u").build();
        Device device = Device.builder().build();

        when(jwtService.extractDeviceId(refreshToken)).thenReturn(deviceId);
        when(jwtService.extractUsername(refreshToken)).thenReturn("u");
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));

        authService.logout(refreshToken);

        verify(refreshTokenRepository).deleteByUserAndDevice(user, device);
        verify(deviceRepository).delete(device);
    }

    // ==================== CHANGE PASSWORD ====================
    @Test
    void changePassword_success() {
        String refreshToken = "token";
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO();
        dto.setOldPassword("old12345");
        dto.setNewPassword("new12345");

        User user = User.builder().password("encoded_old").id(UUID.randomUUID()).build();
        when(jwtService.extractUsername(refreshToken)).thenReturn("u");
        when(userRepository.findByUserName("u")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old12345", "encoded_old")).thenReturn(true);
        when(passwordEncoder.encode("new12345")).thenReturn("encoded_new");

        authService.changePassword(refreshToken, dto);

        verify(userRepository).save(user);
        verify(refreshTokenRepository).deleteAllByUserId(user.getId());
    }

    // ==================== FORGOT PASSWORD ====================
    @Test
    void handleForgotPassword_success() {
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO();
        dto.setUserName("alice");
        User user = User.builder().id(UUID.randomUUID()).email("a@b.com").build();
        PasswordResetOtp otp = new PasswordResetOtp();

        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(user));
        when(otpService.generateOtp(user)).thenReturn(otp);

        authService.handleForgotPassword(dto);

        verify(mailService).sendTextMail("a@b.com", otp);
    }

    // ==================== RESET PASSWORD ====================
    @Test
    void resetPassword_success() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setUsername("alice");
        dto.setNewPassword("newpassword123");
        dto.setOtpCode("123456");

        User user = User.builder().id(UUID.randomUUID()).password("old_encoded").build();

        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(user));
        when(otpService.verifyOtp(user.getId(), "123456")).thenReturn(true);
        when(passwordEncoder.encode("newpassword123")).thenReturn("encoded_new");

        authService.resetPassword(dto);

        verify(userRepository).save(user);
    }
}
