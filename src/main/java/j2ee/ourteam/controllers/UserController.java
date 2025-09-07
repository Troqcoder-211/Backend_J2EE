package j2ee.ourteam.controllers;

import org.springframework.web.bind.annotation.RestController;

import j2ee.ourteam.models.user.CreateUserDto;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("users")
public class UserController {

  @PostMapping
  private ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDto request) {
    return ResponseEntity.ok().body(new Object());
  }

  @PutMapping("/{id}")
  public Object updateUser(@PathVariable String id, @RequestBody Object request) {

    return request;
  }

  @GetMapping("/search")
  private ResponseEntity<?> searchUser(@ModelAttribute Object request) {
    return ResponseEntity.ok(request);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getMethodName(@PathVariable String id) {
    return ResponseEntity.ok().body(new Object());
  }

}
