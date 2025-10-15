package j2ee.ourteam.models.page;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
  private List<T> content;

  private PageableMeta pageable;

  private long totalElements;
  private int totalPages;
  private boolean last;
  private boolean first;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PageableMeta {
    private int pageNumber;
    private int pageSize;
  }

  public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .pageable(PageableMeta.builder()
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .build())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .last(page.isLast())
        .first(page.isFirst())
        .build();
  }
}
