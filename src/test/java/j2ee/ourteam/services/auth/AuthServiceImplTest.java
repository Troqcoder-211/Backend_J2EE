package j2ee.ourteam.services.auth;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.PasswordResetOtp;
import j2ee.ourteam.entities.RefreshToken;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.auth.*;
import j2ee.ourteam.repositories.*;
import j2ee.ourteam.services.mail.IMailService;
import j2ee.ourteam.services.otp.IOtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.EntityExistsException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest extends BaseTest {

    private AuthServiceImpl authService;

    // Các mock service không có sẵn trong BaseTest
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private IOtpService otpService;
    private IMailService mailService;
    private AuthenticationManager authenticationManager;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // khởi tạo tất cả @Mock trong BaseTest

        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);
        otpService = Mockito.mock(IOtpService.class);
        mailService = Mockito.mock(IMailService.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);

        // Khởi tạo AuthServiceImpl với tất cả mock từ BaseTest + các mock mới
        authService = new AuthServiceImpl(
                userRepository,
                deviceRepository,
                refreshTokenRepository,
                presenceRepository,
                passwordEncoder,
                jwtService,
                otpService,
                mailService,
                authenticationManager
        );
    }

    // ==================== REGISTER ====================
    @Test
    void register_success() {
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUserName("newuser");
        dto.setPassword("password123");
        dto.setEmail("newuser@example.com");
        dto.setDisplayName("New User");

        when(userRepository.findByUserName("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        authService.register(dto);

        verify(userRepository).save(any(User.class));
        verify(presenceRepository).save(any());
    }

    @Test
    void register_duplicateUsername() {
        User existingUser = mockUser();
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setUserName(existingUser.getUserName());
        dto.setPassword("password123");
        dto.setEmail("abc@example.com");

        when(userRepository.findByUserName(existingUser.getUserName())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(EntityExistsException.class);
    }

    // ==================== LOGIN ====================
    @Test
    void login_success() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .userName("testuser")
                .password("password123")
                .build();

        // tạo dto
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUserName(user.getUserName());
        dto.setPassword("password123");

        // mock user repo
        when(userRepository.findByUserName(eq(user.getUserName()))).thenReturn(Optional.of(user));

        // khi save device: set id => trả device có id (giả lập DB)
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> {
            Device d = inv.getArgument(0);
            d.setId(UUID.randomUUID()); // GÁN ID NHƯ DB
            return d;
        });

        // stub jwtService — bây giờ deviceId ko null nên any(UUID.class) sẽ match
        when(jwtService.generateAccessToken(eq(user), any(UUID.class))).thenReturn("accessToken123");
        when(jwtService.generateRefreshToken(eq(user), any(UUID.class))).thenReturn("refreshToken123");

        // (tùy chọn) tránh side-effect của auth manager: stub authenticate để ko ném
        when(authenticationManager.authenticate(any())).thenReturn(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        user.getUserName(), user.getPassword(), java.util.List.of()
                )
        );

        // gọi service
        LoginResponseDTO resp = authService.login(dto, null);

        // asserts
        assertThat(resp.getAccessToken()).isEqualTo("accessToken123");
        assertThat(resp.getRefreshToken()).isEqualTo("refreshToken123");
        assertThat(resp.getDeviceId()).isNotNull();
    }


    // ==================== REFRESH TOKEN ====================
    @Test
    void refreshAccessToken_success() {
        User user = mockUser();
        UUID deviceId = UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh123")
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(refreshTokenRepository.findByToken("refresh123")).thenReturn(Optional.of(refreshToken));
        when(jwtService.extractDeviceId("refresh123")).thenReturn(deviceId);
        when(jwtService.generateAccessToken(user, deviceId)).thenReturn("newAccessToken");

        String token = authService.refreshAccessToken("refresh123");
        assertThat(token).isEqualTo("newAccessToken");
    }

    // ==================== CHANGE PASSWORD ====================
    @Test
    void changePassword_success() {
        User user = mockUser();
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO();
        dto.setOldPassword("oldPassword");
        dto.setNewPassword("newPassword123");

        when(jwtService.extractUsername("fakeToken")).thenReturn(user.getUserName());
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        authService.changePassword("fakeToken", dto);

        verify(userRepository).save(user);
        verify(refreshTokenRepository).deleteAllByUserId(user.getId());
    }

    // ==================== LOGOUT ====================
    @Test
    void logout_success() {
        User user = mockUser();
        Device device = mockDevice(user);
        UUID deviceId = device.getId();

        when(jwtService.extractDeviceId("refresh123")).thenReturn(deviceId);
        when(jwtService.extractUsername("refresh123")).thenReturn(user.getUserName());
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));

        authService.logout("refresh123");

        verify(deviceRepository).delete(device);
        verify(refreshTokenRepository).deleteByUserAndDevice(user, device);
    }

    // ==================== FORGOT PASSWORD ====================
    @Test
    void handleForgotPassword_success() {
        User user = mockUser();
        ForgotPasswordRequestDTO dto = new ForgotPasswordRequestDTO();
        dto.setUserName(user.getUserName());

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(otpService.generateOtp(user)).thenReturn(new PasswordResetOtp());

        authService.handleForgotPassword(dto);

        verify(mailService).sendTextMail(eq(user.getEmail()), any());
    }

    // ==================== RESET PASSWORD ====================
    @Test
    void resetPassword_success() {
        User user = mockUser();
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO();
        dto.setUsername(user.getUserName());
        dto.setNewPassword("newPassword123");
        dto.setOtpCode("123456");

        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
        when(otpService.verifyOtp(user.getId(), "123456")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");

        authService.resetPassword(dto);

        verify(userRepository).save(user);
    }
}
