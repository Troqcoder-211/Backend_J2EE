package j2ee.ourteam.services.notification;

import j2ee.ourteam.controllers.WebSocketController;
import j2ee.ourteam.entities.Device;
import j2ee.ourteam.entities.Notification;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.mapping.NotificationMapper;
import j2ee.ourteam.models.notification.CreateNotificationDTO;
import j2ee.ourteam.models.notification.NotificationDTO;
import j2ee.ourteam.models.page.PageFilter;
import j2ee.ourteam.repositories.DeviceRepository;
import j2ee.ourteam.repositories.NotificationRepository;
import j2ee.ourteam.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;
    private DeviceRepository deviceRepository;
    private NotificationMapper notificationMapper;
    private WebSocketController webSocketController;

    private NotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        userRepository = mock(UserRepository.class);
        deviceRepository = mock(DeviceRepository.class);
        notificationMapper = mock(NotificationMapper.class);
        webSocketController = mock(WebSocketController.class);

        service = new NotificationServiceImpl(notificationRepository, userRepository, deviceRepository, notificationMapper);
        service.webSocketController = webSocketController;
    }

    @Test
    void create_shouldSaveAndPushNotification() {
        UUID userId = UUID.randomUUID();
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(userId);

        User user = new User();
        user.setId(userId);

        Device device = new Device();
        device.setId(UUID.randomUUID());

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(deviceRepository.findByUserId(userId)).thenReturn(List.of(device));
        when(notificationMapper.toEntity(dto)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(notificationDTO);

        NotificationDTO result = service.create(dto);

        assertEquals(notificationDTO, result);
        verify(notificationRepository).saveAll(any());
        verify(webSocketController).pushNotification(notificationDTO);
    }

    @Test
    void create_shouldThrowIfUserNotFound() {
        UUID userId = UUID.randomUUID();
        CreateNotificationDTO dto = new CreateNotificationDTO();
        dto.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.create(dto));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void markAsRead_shouldUpdateIsRead() {
        UUID notificationId = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setIsRead(false);

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        NotificationDTO result = service.markAsRead(notificationId);

        assertTrue(notification.getIsRead());
        assertEquals(dto, result);
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsDelivered_shouldUpdateDelivered() {
        UUID notificationId = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setIsDelivered(false);

        NotificationDTO dto = new NotificationDTO();
        dto.setId(notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        NotificationDTO result = service.markAsDelivered(notificationId);

        assertTrue(notification.getIsDelivered());
        assertNotNull(notification.getDeliveredAt());
        assertEquals(dto, result);
        verify(notificationRepository).save(notification);
    }

    @Test
    void deleteById_shouldDeleteNotification() {
        UUID notificationId = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(notificationId);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        assertDoesNotThrow(() -> service.deleteById(notificationId));
        verify(notificationRepository).delete(notification);
    }

    @Test
    void getUserNotifications_shouldReturnPagedResults() {
        PageFilter filter = new PageFilter();
        filter.setPage(1);
        filter.setLimit(10);
        filter.setSortBy("createdAt");
        filter.setSortOrder("DESC");

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());

        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(notificationMapper.toDto(notification)).thenReturn(dto);

        Page<NotificationDTO> result = service.getUserNotifications(UUID.randomUUID(), filter);

        assertEquals(1, result.getTotalElements());
        assertEquals(dto, result.getContent().get(0));
    }
}
