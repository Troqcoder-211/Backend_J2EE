package j2ee.ourteam.services.auth;

import org.springframework.stereotype.Service;

import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.ChangePasswordRequestDTO;
import j2ee.ourteam.models.auth.ForgotPasswordRequestDTO;
import j2ee.ourteam.models.auth.JwtResponseDTO;
import j2ee.ourteam.models.auth.LoginRequestDTO;
import j2ee.ourteam.models.auth.LogoutRequestDTO;
import j2ee.ourteam.models.auth.RefreshRequestDTO;
import j2ee.ourteam.models.auth.RegisterRequestDTO;
import j2ee.ourteam.repositories.UserRepository;
import j2ee.ourteam.utils.PasswordUtils;

@Service
public class AuthService implements IAuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordUtils passwordUtils;

    public AuthService(JwtService jwtService, UserRepository userRepository, PasswordUtils passwordUtils) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordUtils = passwordUtils;
    }

    @Override
    public ApiResponse<?> login(LoginRequestDTO loginRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    @Override
    public ApiResponse<?> refreshToken(RefreshRequestDTO refreshTokenRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }

    @Override
    public ApiResponse<?> register(RegisterRequestDTO registerRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
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
