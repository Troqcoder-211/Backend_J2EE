package j2ee.ourteam.services.auth;

import j2ee.ourteam.models.auth.*;

public interface IAuthService {
    public JwtResponseDTO login(LoginRequestDTO loginRequestDTO);

    public JwtResponseDTO refreshToken(RefreshRequestDTO refreshTokenRequestDTO);

    public void register(RegisterRequestDTO registerRequestDTO);

    public void logout(LogoutRequestDTO logoutRequestDTO);

    public void changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

    public void forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);
    public Object getCurrentUser(String username);
    public void changePassword(String username, ChangePasswordRequestDTO request);
    

}
