package j2ee.ourteam.services.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.ChangePasswordRequestDTO;
import j2ee.ourteam.models.auth.ForgotPasswordRequestDTO;
import j2ee.ourteam.models.auth.JwtResponseDTO;
import j2ee.ourteam.models.auth.LoginRequestDTO;
import j2ee.ourteam.models.auth.LogoutRequestDTO;
import j2ee.ourteam.models.auth.RefreshRequestDTO;
import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.repositories.UserRepository;

@Service
public class AuthServiceImpl implements IAuthService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public ApiResponse<?> login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            var user = userRepository.findByUserName(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

            UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(user.getUserName())
                    .password(user.getPassword())
                    .authorities("USER")
                    .build();

            var accessToken = jwtService.generateAccessToken(userDetails);
            var refreshToken = jwtService.generateRefreshToken(userDetails);

            return new ApiResponse<>(200, "Đăng nhập thành công", new JwtResponseDTO(accessToken, refreshToken));
        } catch (Exception e) {
            return new ApiResponse<>(500, "Lỗi khi đăng nhập", null);
        }

    }

    @Override
    public ApiResponse<?> refreshToken(RefreshRequestDTO refreshTokenRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
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
                    .email(request.getEmail())
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
    public ApiResponse<?> logout(LogoutRequestDTO logoutRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }

    @Override
    public ApiResponse<?> changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public ApiResponse<?> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forgotPassword'");
    }

    @Override
    public Object getCurrentUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentUser'");
    }

    @Override
    public ApiResponse<?> changePassword(String username, ChangePasswordRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

}
