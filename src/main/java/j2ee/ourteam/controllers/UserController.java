package j2ee.ourteam.controllers;

import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.entities.User;
import j2ee.ourteam.models.apiresponse.ApiResponse;
import j2ee.ourteam.models.user.GetUserListRequestDTO;
import j2ee.ourteam.models.user.GetUserRequestDTO;
import j2ee.ourteam.services.user.IUserService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final IUserService userService;

  @GetMapping()
  public ResponseEntity<?> getUserList(GetUserListRequestDTO request) {
    return null;
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserProfile(GetUserRequestDTO request) {
    return null;
  }

  @PutMapping("/me")
  public ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal User currentUser){
    return null;
  }

  @PutMapping("/me/disable")
  public ResponseEntity<?> disable(@AuthenticationPrincipal User currentUser){
    setDisabale(currentUser, true);
    return null;
  }

  @PutMapping("/me/enable")
  public ResponseEntity<?> enable(@AuthenticationPrincipal User currentUser){
    setDisabale(currentUser, false);
    return null;
  }

  @DeleteMapping("/delete/me")
  public ResponseEntity<?> delete(@AuthenticationPrincipal User currentUser){
    return null;
  }

  public ApiResponse<?> setDisabale(User currentUser, boolean isDisable){
    return null;
  }
}
