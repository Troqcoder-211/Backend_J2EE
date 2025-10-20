package j2ee.ourteam.services.auth;

import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.*;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {

    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO request, HttpServletResponse response);

    public ApiResponse<String> refreshAccessToken(String refreshToken);

    public ApiResponse<?> register(RegisterRequestDTO registerRequestDTO);

    public ApiResponse<?> logout(String refreshToken);

    public ApiResponse<?> changePassword(String username, ChangePasswordRequestDTO changePasswordRequestDTO);

    public ApiResponse<?> handleForgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    public ApiResponse<?> resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);

}
