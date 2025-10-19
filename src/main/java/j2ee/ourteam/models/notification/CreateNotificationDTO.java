package j2ee.ourteam.models.notification;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import j2ee.ourteam.enums.notification.NotificationTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNotificationDTO implements Serializable {
    @NotNull(message = "userId isn't empty")
    private UUID userId;

    @NotNull(message = "deviceId isn't empty")
    private UUID deviceId;

    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum type;

    @JsonProperty("payload")
    private Object payload;
}