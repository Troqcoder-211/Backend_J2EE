package j2ee.ourteam.services.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.RefreshToken;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.ChangePasswordRequestDTO;
import j2ee.ourteam.models.auth.ForgotPasswordRequestDTO;
import j2ee.ourteam.models.auth.LoginResponseDTO;
import j2ee.ourteam.models.auth.LoginRequestDTO;
import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.repositories.DeviceRepository;
import j2ee.ourteam.repositories.RefreshTokenRepository;
import j2ee.ourteam.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthServiceImpl implements IAuthService {

    private UserRepository userRepository;

    private DeviceRepository deviceRepository;

    private RefreshTokenRepository refreshTokenRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
            DeviceRepository deviceRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO request, HttpServletResponse response) {
        try {
            // ✅ Xác thực username + password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            // ✅ Lấy user từ DB
            var user = userRepository.findByUserName(request.getUsername())
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
            // 1️⃣ Giải mã refresh token
            String username = jwtService.extractUsername(refreshToken);
            UUID deviceId = jwtService.extractDeviceId(refreshToken);

            // 2️⃣ Kiểm tra DB (nếu bạn lưu refresh token)
            RefreshToken refresh = refreshTokenRepository.findByToken(refreshToken).orElse(null);

            if (refresh == null) {
                return new ApiResponse<>(401, "Refresh token không hợp lệ hoặc bị thu hồi", null);
            }

            if (refresh.getExpiresAt().isBefore(LocalDateTime.now())) {
                return new ApiResponse<>(401, "Refresh token đã hết hạn", null);
            }

            // 3️⃣ Sinh access token mới
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
            if (userRepository.findByUserName(request.getUsername()).isPresent()) {
                return new ApiResponse<>(400, "Tên đăng nhập đã tồn tại", false);
            }
            if (request.getPassword().length() < 8) {
                return new ApiResponse<>(401, "Mật khẩu không hợp lệ", false);
            }

            User user = User.builder()
                    .userName(request.getUsername())
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
    public ApiResponse<?> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forgotPassword'");
    }

}
