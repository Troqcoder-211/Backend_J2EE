package j2ee.ourteam.models.attachment;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentDTO {
  private UUID id;
  private UUID uploaderId;
  private UUID conversationId;
  private String filename;
  private String mimeType;
  private String s3Bucket;
  private String s3Key;
  private String thumbnailS3Key;
  private Long sizeBytes;
  private String checksum;
  private LocalDate createdAt;
}
