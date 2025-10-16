package j2ee.ourteam.models.apiresponse;

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
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;
}
