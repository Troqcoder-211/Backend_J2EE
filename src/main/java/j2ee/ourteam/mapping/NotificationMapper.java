package j2ee.ourteam.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.models.notification.NotificationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "userId", expression = "java(notification.getUser() != null ? notification.getUser().getId() : null)")
    @Mapping(target = "deviceId", expression = "java(notification.getDevice() != null ? notification.getDevice().getId() : null)")
    @Mapping(target = "isDelivered", source = "isDelivered")
    @Mapping(source = "payload", target = "payload", qualifiedByName = "objectToString")
    NotificationDTO toDto(Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "device", ignore = true)
    @Mapping(target = "isDelivered", ignore = true)
    @Mapping(target = "deliveredAt", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "payload", target = "payload", qualifiedByName = "objectToString")
    Notification toEntity(CreateNotificationDTO notificationDTO);

    @Named("objectToString")
    default String objectToString(Object payload) {
        try {
            if (payload == null)
                return null;
            ObjectMapper objectMapper = new ObjectMapper();
            if (payload instanceof String) {
                ObjectNode payloadNode = objectMapper.createObjectNode();
                payloadNode.put("message", (String) payload);
                return objectMapper.writeValueAsString(payloadNode);
            } else {
                return objectMapper.writeValueAsString(payload);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload to JSON", e);
        }
    }
}