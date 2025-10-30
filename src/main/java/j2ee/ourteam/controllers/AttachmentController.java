package j2ee.ourteam.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
import j2ee.ourteam.services.attachment.IAttachmentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("attachments")
@RequiredArgsConstructor
public class AttachmentController {

  @Autowired
  private final IAttachmentService attachmentService;

  @PostMapping("/upload")
  public ResponseEntity<AttachmentDTO> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "conversationId", required = false) UUID conversationId,
      @RequestParam(value = "uploaderId", required = false) UUID uploaderId) {

    AttachmentDTO attachment = attachmentService.uploadFile(file,
        uploaderId, conversationId);

    return ResponseEntity.ok(attachment);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AttachmentDTO> getMetaDataFile(@PathVariable UUID id) {
    return ResponseEntity.ok(attachmentService.getMetaData(id));
  }

  @GetMapping("/{id}/download")
  public void downloadFile(@PathVariable UUID id) {
    attachmentService.downloadFile(id);

    // return ResponseEntity.ok()
    // .contentType(MediaType.parseMediaType(file.getMimeType()))
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
    // file.getFilename() + "\"")
    // .body(file.getResource());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
    attachmentService.deleteAttachment(id);
    return ResponseEntity.noContent().build();
  }

}
