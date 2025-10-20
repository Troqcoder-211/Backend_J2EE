package j2ee.ourteam.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GetUserListRequestDTO {
    private String userName;
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int limit = 10;
}
