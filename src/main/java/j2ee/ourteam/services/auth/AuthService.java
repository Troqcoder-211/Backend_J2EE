package j2ee.ourteam.services.auth;

import org.springframework.stereotype.Service;

import j2ee.ourteam.models.auth.ChangePasswordRequestDTO;
import j2ee.ourteam.models.auth.ForgotPasswordRequestDTO;
import j2ee.ourteam.models.auth.JwtResponseDTO;
import j2ee.ourteam.models.auth.LoginRequestDTO;
import j2ee.ourteam.models.auth.LogoutRequestDTO;
import j2ee.ourteam.models.auth.RefreshRequestDTO;
import j2ee.ourteam.models.auth.RegisterRequestDTO;

@Service
public class AuthService implements IAuthService {

    @Override
    public JwtResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    @Override
    public JwtResponseDTO refreshToken(RefreshRequestDTO refreshTokenRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refreshToken'");
    }

    @Override
    public void register(RegisterRequestDTO registerRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'register'");
    }

    @Override
    public void logout(LogoutRequestDTO logoutRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }

    @Override
    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forgotPassword'");
    }

    public Object getCurrentUser(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentUser'");
    }

    public void changePassword(String username, ChangePasswordRequestDTO request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePassword'");
    }

    public void logout(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }
    
}
