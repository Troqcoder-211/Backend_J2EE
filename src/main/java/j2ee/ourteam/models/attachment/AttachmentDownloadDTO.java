package j2ee.ourteam.models.attachment;

import org.springframework.core.io.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentDownloadDTO {
  private String filename;
  private String mimeType;
  private Resource resource;
}