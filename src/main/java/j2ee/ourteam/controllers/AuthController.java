package j2ee.ourteam.controllers;

import java.util.Arrays;

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
import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.auth.*;
import j2ee.ourteam.models.user.UserResponseDTO;
import j2ee.ourteam.services.auth.IAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService authService;

    // Đăng ký tài khoản mới
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            var response = authService.register(request);
            return ResponseEntity.status(response.getStatusCode()).body(response.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi đăng ký");
        }
    }

    // Đăng nhập → trả về accessToken + refreshToken
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse response) {
        try {

            ApiResponse<LoginResponseDTO> result = authService.login(request, response);

            if (result.getStatusCode() == 200 && result.getData() != null) {
                LoginResponseDTO data = result.getData();

                // ✅ Tạo cookie Access Token
                Cookie accessTokenCookie = new Cookie("access_token", data.getAccessToken());
                accessTokenCookie.setHttpOnly(true);
                // accessTokenCookie.setSecure(true); // nên bật nếu dùng HTTPS
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge(15 * 60); // 15 phút
                accessTokenCookie.setAttribute("SameSite", "None");

                // ✅ Tạo cookie Refresh Token
                Cookie refreshTokenCookie = new Cookie("refresh_token", data.getRefreshToken());
                refreshTokenCookie.setHttpOnly(true);
                // refreshTokenCookie.setSecure(true);
                refreshTokenCookie.setPath("/auth/refresh");
                refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 7 ngày
                refreshTokenCookie.setAttribute("SameSite", "None");

                // ✅ Gửi cookie về client
                response.addCookie(accessTokenCookie);
                response.addCookie(refreshTokenCookie);

                return ResponseEntity.ok(new ApiResponse<>(
                        200,
                        "Đăng nhập thành công",
                        new LoginResponseDTO(null, null, data.getDeviceId())));
            }
            return ResponseEntity.status(result.getStatusCode()).body(result.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi đăng nhập");
        }
    }

    // Làm mới token bằng refresh token
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1️⃣ Lấy refresh token từ cookie
            String refreshToken = extractCookieValue(request, "refresh_token");
            if (refreshToken == null) {
                return ResponseEntity.status(401).body("Không tìm thấy refresh token");
            }

            // 2️⃣ Xử lý trong service
            ApiResponse<String> result = authService.refreshAccessToken(refreshToken);

            if (result.getStatusCode() != 200 || result.getData() == null) {
                return ResponseEntity.status(result.getStatusCode()).body(result.getMessage());
            }

            // 3️⃣ Gửi lại access token mới qua cookie
            Cookie newAccessTokenCookie = new Cookie("access_token", result.getData());
            newAccessTokenCookie.setHttpOnly(true);
            newAccessTokenCookie.setPath("/");
            newAccessTokenCookie.setMaxAge(60 * 15);
            newAccessTokenCookie.setAttribute("SameSite", "None");
            newAccessTokenCookie.setSecure(true);

            response.addCookie(newAccessTokenCookie);

            return ResponseEntity.ok("Làm mới token thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi làm mới token: " + e.getMessage());
        }
    }

    // Đăng xuất (xóa refresh token khỏi DB nếu có lưu)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {

            String refreshToken = extractCookieValue(request, "refresh_token");
            if (refreshToken == null) {
                return ResponseEntity.status(400).body("Không tìm thấy refresh_token");
            }

            ApiResponse<?> result = authService.logout(refreshToken);

            // ✅ Xóa cookie ở trình duyệt
            Cookie accessCookie = new Cookie("access_token", "");
            accessCookie.setPath("/");
            accessCookie.setMaxAge(0);
            accessCookie.setHttpOnly(true);
            // accessCookie.setSecure(true);
            accessCookie.setAttribute("SameSite", "None");

            Cookie refreshCookie = new Cookie("refresh_token", "");
            refreshCookie.setPath("/auth/refresh");
            refreshCookie.setMaxAge(0);
            refreshCookie.setHttpOnly(true);
            // refreshCookie.setSecure(true);
            refreshCookie.setAttribute("SameSite", "None");

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            return ResponseEntity.status(result.getStatusCode()).body(result.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi đăng xuất");
        }
    }

    // Quên mật khẩu
    // @PostMapping("/forgot-password")
    // public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO
    // request) {
    // authService.forgotPassword(request);
    // return ResponseEntity.ok("Reset link sent to your email");
    // }

    // Đổi mật khẩu (khi đã đăng nhập)
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request,
            HttpServletResponse httpResponse) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            String username = authentication.getName();

            ApiResponse<?> result = authService.changePassword(username, request);

            if (result.getStatusCode() == 200) {
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
            }

            return ResponseEntity.status(result.getStatusCode()).body(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi thay đổi mật khẩu");
        }
    }

    // Lấy thông tin người dùng hiện tại
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.status(200).body(new UserResponseDTO(currentUser));
    }

    // Hàm tiện ích lấy username từ JWT (qua SecurityContext)
    // private String getCurrentUserName() {
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // if (auth == null || !auth.isAuthenticated()) {
    // throw new RuntimeException("User not authenticated");
    // }
    // return auth.getName();
    // }

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
