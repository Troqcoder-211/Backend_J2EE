package j2ee.ourteam.services.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import j2ee.ourteam.entities.*;
import j2ee.ourteam.repositories.PresenceRepository;
import j2ee.ourteam.services.mail.IMailService;
import j2ee.ourteam.services.otp.IOtpService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import j2ee.ourteam.models.auth.ChangePasswordRequestDTO;
import j2ee.ourteam.models.auth.ForgotPasswordRequestDTO;
import j2ee.ourteam.models.auth.LoginResponseDTO;
import j2ee.ourteam.models.auth.LoginRequestDTO;
import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.models.auth.ResetPasswordRequestDTO;
import j2ee.ourteam.repositories.DeviceRepository;
import j2ee.ourteam.repositories.RefreshTokenRepository;
import j2ee.ourteam.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;

    private final DeviceRepository deviceRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PresenceRepository presenceRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final IOtpService otpService;

    private final IMailService mailService;

    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {
        var user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản không tồn tại"));

        boolean isCorrectPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!isCorrectPassword) {
            throw new SecurityException("Mật khẩu không đúng");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()));

        Device device = Device.builder()
                .user(user)
                .deviceType(request.getDeviceType())
                .pushToken(request.getPushToken())
                .lastSeenAt(LocalDateTime.now())
                .build();
        deviceRepository.save(device);

        var accessToken = jwtService.generateAccessToken(user, device.getId());
        var refreshToken = jwtService.generateRefreshToken(user, device.getId());

        RefreshToken refreshTokendb = RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .device(device)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshTokendb);

        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setAccessToken(accessToken);
        dto.setRefreshToken(refreshToken);
        dto.setDeviceId(device.getId());

        return dto;
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        UUID deviceId = jwtService.extractDeviceId(refreshToken);

        RefreshToken refresh = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ hoặc bị thu hồi"));

        if (refresh.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        User user = refresh.getUser();
        return jwtService.generateAccessToken(user, deviceId);
    }

    @Override
    public void register(RegisterRequestDTO request) {
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new EntityExistsException("Tên đăng nhập đã tồn tại");
        }
        if (request.getPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu không hợp lệ");
        }

        User user = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .avatarS3Key("")
                .email(request.getEmail())
                .isDisabled(false)
                .build();

        userRepository.save(user);

        Presence presence = Presence.builder()
                .user(user)
                .isOnline(false)
                .lastSeenAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        presenceRepository.save(presence);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Token không hợp lệ");
        }

        Device device = deviceRepository.findById(jwtService.extractDeviceId(refreshToken))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

        User user = userRepository.findByUserName(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        refreshTokenRepository.deleteByUserAndDevice(user, device);
        deviceRepository.delete(device);
    }

    @Override
    public void changePassword(String refreshToken, ChangePasswordRequestDTO dto) {
        if (dto.getNewPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu không hợp lệ");
        }

        User user = userRepository.findByUserName(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không trùng khớp");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        refreshTokenRepository.deleteAllByUserId(user.getId());
    }

    @Override
    public void handleForgotPassword(ForgotPasswordRequestDTO dto) {
        User user = userRepository.findByUserName(dto.getUserName())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        PasswordResetOtp otp = otpService.generateOtp(user);
        mailService.sendTextMail(user.getEmail(), otp);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDTO dto) {
        if (dto.getNewPassword().length() < 8) {
            throw new RuntimeException("Mật khẩu không hợp lệ");
        }

        User user = userRepository.findByUserName(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        boolean isOtpValid = otpService.verifyOtp(user.getId(), dto.getOtpCode());
        if (!isOtpValid) {
            throw new RuntimeException("OTP không hợp lệ hoặc đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

}
