package j2ee.ourteam.controllers;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import j2ee.ourteam.models.attachment.AttachmentDTO;

@RestController
@RequestMapping("attachments")
public class AttachmentController {

  @PostMapping("/upload")
  public ResponseEntity<AttachmentDTO> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "conversationId", required = false) UUID conversationId) {
    // AttachmentDTO attachment = attachmentService.uploadFile(file,
    // currentUser.getId(), conversationId);
    // return ResponseEntity.ok(attachment);
    return ResponseEntity.ok(new AttachmentDTO());
  }

  @GetMapping("/{id}")
  public String getMetaDataFile(@PathVariable UUID id) {
    return new String();
  }

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) {
    // AttachmentDownloadDTO response = attachmentService.downloadFile(id);

    // return ResponseEntity.ok(new Resource() {

    // }));
    // .contentType(MediaType.parseMediaType(response.getMimeType()))
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
    // response.getFilename() + "\"")
    // .body(response.getResource());
    return null;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
    // attachmentService.deleteAttachment(id);
    return ResponseEntity.noContent().build();

  }

}
