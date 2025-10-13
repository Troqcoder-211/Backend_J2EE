package j2ee.ourteam.models.page;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
  private List<T> items;
  private Integer page;
  private Integer limit;
  private Integer totalPages;
  private Long totalItems;
}
