package j2ee.ourteam.models.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PageFilter {
  @Builder.Default
  @Min(1)
  private Integer page = 1;

  @Builder.Default
  @Max(100)
  private Integer limit = 10;

  @Builder.Default
  private String sortBy = "id";

  @Builder.Default
  private String sortOrder = "asc";

  public void normalize() {
    if (page == null || page < 1)
      page = 1;
    if (limit == null || limit < 1)
      limit = 10;
    if (sortBy == null)
      sortBy = "id";
    if (sortOrder == null || (!sortOrder.equalsIgnoreCase("asc") && !sortOrder.equalsIgnoreCase("desc")))
      sortOrder = "asc";
  }

}
