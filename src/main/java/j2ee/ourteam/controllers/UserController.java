package j2ee.ourteam.controllers;

import org.springframework.web.bind.annotation.*;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.UpdateUserProfileDTO;
import j2ee.ourteam.services.user.IUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;

  // Lấy danh sách user, có phân trang + tìm kiếm theo username
  @GetMapping
  public ResponseEntity<?> getUserList(@ModelAttribute GetUserListRequestDTO request) {
    var result = userService.getUserList(request);
    return ResponseEntity.ok(result);
  }

  // Lấy profile
  @GetMapping("/{id}")
  public ResponseEntity<?> getUserProfile(@PathVariable UUID id) {
    var user = userService.getUserProfile(id);
    return ResponseEntity.ok(user);
  }

  // Cập nhật profile
  @PatchMapping("/me")
  public ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal User currentUser,
      @RequestBody UpdateUserProfileDTO dto) {
    var updated = userService.updateMyProfile(currentUser.getId(), dto);
    return ResponseEntity.ok(updated);
  }

  @PostMapping("/me/avatar")
  public ResponseEntity<?> updateAvatar(@AuthenticationPrincipal User currentUser,
      @RequestParam("file") MultipartFile file) throws IOException {
    var avatarUrl = userService.updateAvatar(currentUser.getId(), file);
    return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
  }

  // Vô hiệu hóa tài khoản
  @PatchMapping("/me/disable")
  public ResponseEntity<?> disable(@AuthenticationPrincipal User currentUser) {
    userService.disableUser(currentUser.getId());
    return ResponseEntity.ok(Map.of("message", "Tài khoản đã bị vô hiệu hóa"));
  }

  // Kích hoạt lại tài khoản
  @PatchMapping("/me/enable")
  public ResponseEntity<?> enable(@AuthenticationPrincipal User currentUser) {
    userService.enableUser(currentUser.getId());
    return ResponseEntity.ok(Map.of("message", "Tài khoản đã được kích hoạt"));
  }

  // Xóa tài khoản
  @DeleteMapping("/delete/me")
  public ResponseEntity<?> delete(@AuthenticationPrincipal User currentUser) {
    userService.deleteUser(currentUser.getId());
    return ResponseEntity.ok(Map.of("message", "Xóa tài khoản thành công"));
  }
}
