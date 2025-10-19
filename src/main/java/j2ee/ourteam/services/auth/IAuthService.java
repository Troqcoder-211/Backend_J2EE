package j2ee.ourteam.services.auth;

import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.*;

public interface IAuthService {

    public ApiResponse<?> login(LoginRequestDTO loginRequestDTO);

    public ApiResponse<?> refreshToken(RefreshRequestDTO refreshTokenRequestDTO);

    public ApiResponse<?> register(RegisterRequestDTO registerRequestDTO);

    public ApiResponse<?> logout(LogoutRequestDTO logoutRequestDTO);

    public ApiResponse<?> changePassword(ChangePasswordRequestDTO changePasswordRequestDTO);

    public ApiResponse<?> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

    public Object getCurrentUser(String username);

    public ApiResponse<?> changePassword(String username, ChangePasswordRequestDTO request);

}
