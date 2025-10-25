package j2ee.ourteam.controllers;

import java.util.Arrays;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.UserMapper;
import j2ee.ourteam.models.auth.*;
import j2ee.ourteam.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    private final UserMapper userMapper;

    // Đăng ký tài khoản mới
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok(Map.of("message","Đăng ký thành công"));
    }

    // Đăng nhập → trả về accessToken + refreshToken
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {

        LoginResponseDTO data = authService.login(request, response);

        Cookie accessTokenCookie = new Cookie("access_token", data.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60);
        accessTokenCookie.setAttribute("SameSite", "None");
        // accessTokenCookie.setSecure(true);

        Cookie refreshTokenCookie = new Cookie("refresh_token", data.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/auth/refresh");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshTokenCookie.setAttribute("SameSite", "None");
        // refreshTokenCookie.setSecure(true);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(Map.of("deviceId", data.getDeviceId()));
    }


    // Làm mới token bằng refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, "refresh_token");
        if (refreshToken == null) {
            throw new RuntimeException("Không tìm thấy refresh token");
        }

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        Cookie newAccessTokenCookie = new Cookie("access_token", newAccessToken);
        newAccessTokenCookie.setHttpOnly(true);
        newAccessTokenCookie.setPath("/");
        newAccessTokenCookie.setMaxAge(60 * 15);
        newAccessTokenCookie.setAttribute("SameSite", "None");
        newAccessTokenCookie.setSecure(true);

        response.addCookie(newAccessTokenCookie);

        return ResponseEntity.ok(Map.of("message","Làm mới token thành công"));
    }


    // Đăng xuất (xóa refresh token khỏi DB)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, "refresh_token");
        if (refreshToken == null) {
            throw new RuntimeException("Không tìm thấy refresh token");
        }

        authService.logout(refreshToken);

        // ✅ Xóa cookie ở trình duyệt
        Cookie accessCookie = new Cookie("access_token", "");
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);
        accessCookie.setAttribute("SameSite", "None");

        Cookie refreshCookie = new Cookie("refresh_token", "");
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setAttribute("SameSite", "None");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of("message","Đăng xuất thành công"));
    }

    // Quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
        authService.handleForgotPassword(request);
        return ResponseEntity.ok(Map.of("message","Đã gửi OTP! Hãy kiểm tra email của bạn"));
    }

    // Đặt lại mật khẩu
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message","Bạn đã đặt lại mật khẩu"));
    }

    // Đổi mật khẩu (khi đã đăng nhập)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request,
            HttpServletResponse httpResponse) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();

        authService.changePassword(username, request);

        // Xóa cookie khi đổi mật khẩu thành công
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setAttribute("SameSite", "None");

        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "None");

        httpResponse.addCookie(accessCookie);
        httpResponse.addCookie(refreshCookie);


        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }
    // Lấy thông tin người dùng hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.status(200).body(userMapper.toUserProfileResponseDTO(currentUser));
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null)
            return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
