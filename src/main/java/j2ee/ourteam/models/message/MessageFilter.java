package j2ee.ourteam.models.message;

import java.util.UUID;

import j2ee.ourteam.models.page.PageFilter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MessageFilter extends PageFilter {
  @Size(max = 100, message = "Keyword must be at most 100 characters")
  private String keyword;

  @org.hibernate.validator.constraints.UUID
  private UUID conversationId; // lọc theo hội thoại

  @org.hibernate.validator.constraints.UUID
  private UUID senderId; // lọc theo người gửi
}
