package j2ee.ourteam.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_reads")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRead {
  @Builder.Default
  @EmbeddedId
  private MessageReadId id = new MessageReadId();

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("messageId")
  @JoinColumn(name = "message_id", nullable = false)
  private Message message;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Builder.Default
  @Column(name = "read_at", nullable = false)
  private LocalDate readAt = LocalDate.now();
}
