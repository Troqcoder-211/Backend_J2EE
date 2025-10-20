package j2ee.ourteam.controllers;

import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.GetUserRequestDTO;
import j2ee.ourteam.models.user.UpdateUserProfileDTO;
import j2ee.ourteam.services.user.IUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;

  //lấy danh sách user, có phân trang + tìm kiếm theo username
  @GetMapping
  public ResponseEntity<?> getUserList(@ModelAttribute GetUserListRequestDTO request) {
    var result = userService.getUserList(request);
    return ResponseEntity.ok(new ApiResponse<>(200, "OK", result));
  }

  //lấy profile
  @GetMapping("/{id}")
  public ResponseEntity<?> getUserProfile(@RequestBody @ModelAttribute GetUserRequestDTO request) {
    var user = userService.getUserProfile(request.getUserId());
    return ResponseEntity.ok(new ApiResponse<>(200, "OK", user));
  }

  //cập nhật profile
  @PutMapping("/me")
  public ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal User currentUser,
      @RequestBody UpdateUserProfileDTO dto) {
    var updated = userService.updateMyProfile(currentUser.getId(), dto);
    return ResponseEntity.ok(new ApiResponse<>(200, "Cập nhật thành công", updated));
  }

  @PutMapping("/me/disable")
  public ResponseEntity<?> disable(@AuthenticationPrincipal User currentUser) {
    userService.disableUser(currentUser.getId());
    return ResponseEntity.ok(new ApiResponse<>(200, "Tài khoản đã bị vô hiệu hóa", null));
  }

  @PutMapping("/me/enable")
  public ResponseEntity<?> enable(@AuthenticationPrincipal User currentUser) {
    userService.enableUser(currentUser.getId());
    return ResponseEntity.ok(new ApiResponse<>(200, "Tài khoản đã được kích hoạt", null));
  }

  @DeleteMapping("/delete/me")
  public ResponseEntity<?> delete(@AuthenticationPrincipal User currentUser) {
    userService.deleteUser(currentUser.getId());
    return ResponseEntity.ok(new ApiResponse<>(200, "Xóa tài khoản thành công", null));
  }
}
