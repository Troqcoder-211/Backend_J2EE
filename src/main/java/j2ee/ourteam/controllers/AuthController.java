package j2ee.ourteam.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import j2ee.ourteam.models.auth.*;
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
        var response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    // Đăng nhập → trả về accessToken + refreshToken
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // Làm mới token bằng refresh token
    // @PostMapping("/refresh")
    // public ResponseEntity<?> refreshToken(@RequestBody RefreshRequestDTO request) {
    //     var response = authService.refreshToken(request);
    //     return ResponseEntity.ok(response);
    // }

    // Đăng xuất (xóa refresh token khỏi DB nếu có lưu)
    // @PostMapping("/logout")
    // public ResponseEntity<?> logout() {
    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     if (auth != null) {
    //         authService.logout(auth.getName());
    //     }
    //     return ResponseEntity.ok("Logged out successfully");
    // }

    // Quên mật khẩu
    // @PostMapping("/forgot-password")
    // public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO request) {
    //     authService.forgotPassword(request);
    //     return ResponseEntity.ok("Reset link sent to your email");
    // }

    // Đổi mật khẩu (khi đã đăng nhập)
    // @PostMapping("/change-password")
    // public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequestDTO request) {
    //     String username = getCurrentUserName();
    //     authService.changePassword(username, request);
    //     return ResponseEntity.ok("Password changed successfully");
    // }

    // Lấy thông tin người dùng hiện tại
    // @GetMapping("/me")
    // public ResponseEntity<?> getCurrentUser() {
    //     String username = getCurrentUserName();
    //     var user = authService.getCurrentUser(username);
    //     return ResponseEntity.ok(user);
    // }

    // Hàm tiện ích lấy username từ JWT (qua SecurityContext)
    // private String getCurrentUserName() {
    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     if (auth == null || !auth.isAuthenticated()) {
    //         throw new RuntimeException("User not authenticated");
    //     }
    //     return auth.getName();
    // }
}
