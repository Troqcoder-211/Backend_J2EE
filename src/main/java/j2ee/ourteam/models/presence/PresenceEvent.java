package j2ee.ourteam.models.presence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresenceEvent implements Serializable {
    private UUID userId;
    private String status;
    private LocalDateTime timestamp;
}