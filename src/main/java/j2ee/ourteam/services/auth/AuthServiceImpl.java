package j2ee.ourteam.services.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.PasswordResetOtp;
import j2ee.ourteam.entities.RefreshToken;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.ChangePasswordRequestDTO;
import j2ee.ourteam.models.auth.ForgotPasswordRequestDTO;
import j2ee.ourteam.models.auth.LoginResponseDTO;
import j2ee.ourteam.models.auth.LoginRequestDTO;
import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.models.auth.ResetPasswordRequestDTO;
import j2ee.ourteam.repositories.DeviceRepository;
import j2ee.ourteam.repositories.RefreshTokenRepository;
import j2ee.ourteam.repositories.UserRepository;
import j2ee.ourteam.services.mail.MailServiceImpl;
import j2ee.ourteam.services.otp.OtpServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;

    private final DeviceRepository deviceRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final OtpServiceImpl otpService;

    private final MailServiceImpl mailService;

    private final AuthenticationManager authenticationManager;

    @Override
    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO request, HttpServletResponse response) {
        try {
            // ✅ Xác thực username + password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()));

            // ✅ Lấy user từ DB
            var user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            // ✅ Lưu thông tin thiết bị
            Device device = Device.builder()
                    .user(user)
                    .deviceType(request.getDeviceType())
                    .pushToken(request.getPushToken())
                    .lastSeenAt(LocalDateTime.now())
                    .build();
            deviceRepository.save(device);

            // ✅ Tạo token có deviceId
            var accessToken = jwtService.generateAccessToken(user, device.getId());
            var refreshToken = jwtService.generateRefreshToken(user, device.getId());

            // ✅ Lưu refresh token vào DB
            RefreshToken refreshTokendb = RefreshToken.builder()
                    .token(refreshToken)
                    .user(user)
                    .device(device)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build();
            refreshTokenRepository.save(refreshTokendb);

            // ✅ Trả lại dữ liệu cần thiết
            LoginResponseDTO dto = new LoginResponseDTO();
            dto.setAccessToken(accessToken);
            dto.setRefreshToken(refreshToken);
            dto.setDeviceId(device.getId());

            return new ApiResponse<>(200, "Đăng nhập thành công", dto);

        } catch (BadCredentialsException e) {
            return new ApiResponse<>(401, "Sai tên đăng nhập hoặc mật khẩu", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Lỗi hệ thống: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> refreshAccessToken(String refreshToken) {

        try {
            UUID deviceId = jwtService.extractDeviceId(refreshToken);

            RefreshToken refresh = refreshTokenRepository.findByToken(refreshToken).orElse(null);

            if (refresh == null) {
                return new ApiResponse<>(401, "Refresh token không hợp lệ hoặc bị thu hồi", null);
            }

            if (refresh.getExpiresAt().isBefore(LocalDateTime.now())) {
                return new ApiResponse<>(401, "Refresh token đã hết hạn", null);
            }

            User user = refresh.getUser();
            String newAccessToken = jwtService.generateAccessToken(user, deviceId);

            return new ApiResponse<>(200, "Tạo access token mới thành công", newAccessToken);

        } catch (Exception e) {
            return new ApiResponse<>(500, "Lỗi khi làm mới token: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<?> register(RegisterRequestDTO request) {
        try {
            if (userRepository.findByUserName(request.getUserName()).isPresent()) {
                return new ApiResponse<>(400, "Tên đăng nhập đã tồn tại", false);
            }
            if (request.getPassword().length() < 8) {
                return new ApiResponse<>(401, "Mật khẩu không hợp lệ", false);
            }

            User user = User.builder()
                    .userName(request.getUserName())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .displayName(request.getDisplayName())
                    .avatarS3Key("")
                    .build();

            userRepository.save(user);

            return new ApiResponse<>(200, "Tạo tài khoản thành công", true);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Lỗi khi đăng ký", false);
        }

    }

    @Override
    public ApiResponse<?> logout(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return new ApiResponse<>(400, "Token không hợp lệ", null);
            }

            Device device = deviceRepository.findById(jwtService.extractDeviceId(refreshToken)).orElse(null);
            User user = userRepository.findByUserName(jwtService.extractUsername(refreshToken)).orElse(null);

            if (device == null || user == null) {
                return new ApiResponse<>(400, "Token không chứa thông tin thiết bị", null);
            }

            refreshTokenRepository.deleteByUserAndDevice(user, device);
            deviceRepository.delete(device);
            return new ApiResponse<>(200, "Đăng xuất thành công", null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Lỗi khi logout", null);
        }

    }

    @Override
    public ApiResponse<?> changePassword(String username, ChangePasswordRequestDTO changePasswordRequestDTO) {
        try {
            if (changePasswordRequestDTO.getNewPassword().length() < 8) {
                return new ApiResponse<>(401, "Mật khẩu không hợp lệ", false);
            }

            User user = userRepository.findByUserName(username).orElse(null);
            if (user == null) {
                return new ApiResponse<>(404, "Không tìm thấy người dùng", null);
            }
            if (passwordEncoder.matches(changePasswordRequestDTO.getOldPassword(), user.getPassword())) {
                return new ApiResponse<>(400, "Mật khẩu không trùng khớp", null);
            }
            user.setPassword(passwordEncoder.encode(changePasswordRequestDTO.getNewPassword()));
            userRepository.save(user);

            refreshTokenRepository.deleteAllByUserId(user.getId());

            return new ApiResponse<String>(200, "Đổi mật khẩu thành công", null);
        } catch (Exception e) {
            return new ApiResponse<String>(500, "Lỗi khi đổi mật khẩu", null);
        }
    }

    @Override
    public ApiResponse<?> handleForgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        try {
            Optional<User> optUser = userRepository.findByUserName(forgotPasswordRequestDTO.getUserName());
            if (optUser.isEmpty()) {
                return new ApiResponse<>(404, "Không tìm thấy người dùng", null);
            }
            User user = optUser.get();
            PasswordResetOtp otp = otpService.generateOtp(user);
            mailService.sendTextMail(user.getEmail(), otp);
            return new ApiResponse<>(200, "Đã gửi OTP đến email của bạn", null);
        } catch (Exception e) {
            return new ApiResponse<String>(500, "Lỗi xử lí", null);
        }
    }

    @Override
    public ApiResponse<?> resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        try {
            if (resetPasswordRequestDTO.getNewPassword().length() < 8) {
                return new ApiResponse<>(401, "Mật khẩu không hợp lệ", false);
            }

            Optional<User> optUser = userRepository.findByUserName(resetPasswordRequestDTO.getUsername());
            if (optUser.isEmpty()) {
                return new ApiResponse<>(404, "Không tìm thấy người dùng", null);
            }
            User user = optUser.get();

            boolean isOtpValid = otpService.verifyOtp(user.getId(), resetPasswordRequestDTO.getOtpCode());
            if (!isOtpValid) {
                return new ApiResponse<>(400, "OTP không hợp lệ hoặc đã hết hạn", false);
            }

            String hashedPassword = passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return new ApiResponse<>(200, "Bạn đã đặt lại mật khẩu thành công", null);
        } catch (Exception e) {
            return new ApiResponse<String>(500, "Lỗi xử lí", null);
        }
    }

}
