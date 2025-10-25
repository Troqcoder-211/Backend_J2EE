package j2ee.ourteam.services.auth;

import j2ee.ourteam.models.auth.*;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {

    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response);

    public String refreshAccessToken(String refreshToken);

    public void register(RegisterRequestDTO registerRequestDTO);

    public void logout(String refreshToken);

    public void changePassword(String username, ChangePasswordRequestDTO changePasswordRequestDTO);

    public void handleForgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);

}
