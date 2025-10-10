package j2ee.ourteam.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import j2ee.ourteam.services.aws.S3Service;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/s3")
@AllArgsConstructor
public class S3Controller {

  private final S3Service s3Service;

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
    String fileUrl = s3Service.uploadFile(file);
    return ResponseEntity.ok(fileUrl);
  }

  @DeleteMapping("/delete/{fileName}")
  public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
    s3Service.deleteFile(fileName);
    return ResponseEntity.ok("Deleted file: " + fileName);
  }
}
