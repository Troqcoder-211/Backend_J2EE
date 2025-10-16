package j2ee.ourteam.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uploader_id")
  private User uploader;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id")
  private Conversation conversation;

  @Column(name = "filename")
  private String filename;

  @Column(name = "mime_type")
  private String mimeType;

  @Column(name = "s3_bucket", nullable = false)
  private String s3Bucket;

  @Column(name = "s3_key", nullable = false)
  private String s3Key;

  @Column(name = "thumbnail_s3_key")
  private String thumbnailS3Key;

  @Column(name = "size_bytes")
  private Long sizeBytes;

  @Column(name = "checksum")
  private String checksum;

  @Builder.Default
  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt = LocalDate.now();

  @Builder.Default
  @ManyToMany(mappedBy = "attachments")
  private Set<Message> messages = new HashSet<>();

}
