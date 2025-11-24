//package j2ee.ourteam.services.notification;
//
//import j2ee.ourteam.BaseTest;
//import j2ee.ourteam.entities.Device;
//import j2ee.ourteam.entities.Notification;
//import j2ee.ourteam.entities.User;
//import j2ee.ourteam.enums.notification.NotificationTypeEnum;
//import j2ee.ourteam.models.notification.CreateNotificationDTO;
//import j2ee.ourteam.models.notification.NotificationDTO;
//import j2ee.ourteam.models.page.PageFilter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.*;
//
//class NotificationServiceImplTest extends BaseTest {
//
//    @InjectMocks
//    private NotificationServiceImpl notificationService;
//
//    private User testUser;
//    private Device testDevice;
//    private Notification testNotification;
//    private CreateNotificationDTO createNotificationDTO;
//    private NotificationDTO notificationDTO;
//
//    @BeforeEach
//    void setUp() {
//        // Khởi tạo test data
//        testUser = mockUser();
//        testDevice = mockDevice(testUser);
//        testNotification = mockNotification(testUser);
//        createNotificationDTO = mockCreateNotificationDTO(testUser.getId());
//
//        notificationDTO = NotificationDTO.builder()
//                .id(testNotification.getId())
//                .userId(testUser.getId())
//                .type(NotificationTypeEnum.MESSAGE)
//                .payload("Test payload")
//                .isRead(false)
//                .isDelivered(false)
//                .createdAt(now())
//                .build();
//    }
//
//    // ==================== CREATE TESTS ====================
//
//    @Test
//    void create_Success() {
//        // Arrange
//        mockUserRepository(testUser);
//        when(deviceRepository.findByUserId(testUser.getId())).thenReturn(List.of(testDevice));
//        when(notificationMapper.toEntity(any(CreateNotificationDTO.class))).thenReturn(testNotification);
//        when(notificationRepository.saveAll(anyList())).thenReturn(List.of(testNotification));
//        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDTO);
//        doNothing().when(webSocketController).pushNotification(any(NotificationDTO.class));
//
//        // Act
//        NotificationDTO result = notificationService.create(createNotificationDTO);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(notificationDTO.getId(), result.getId());
//        assertEquals(notificationDTO.getPayload(), result.getPayload());
//
//        verify(userRepository, times(1)).findById(testUser.getId());
//        verify(deviceRepository, times(1)).findByUserId(testUser.getId());
//        verify(notificationRepository, times(1)).saveAll(anyList());
//        verify(webSocketController, times(1)).pushNotification(any(NotificationDTO.class));
//    }
//
//    @Test
//    void create_UserNotFound_ThrowsException() {
//        // Arrange
//        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResponseStatusException.class, () -> {
//            notificationService.create(createNotificationDTO);
//        });
//
//        verify(userRepository, times(1)).findById(any(UUID.class));
//        verify(notificationRepository, never()).saveAll(anyList());
//    }
//
//    @Test
//    void create_InvalidDTOType_ThrowsException() {
//        // Arrange
//        Object invalidDTO = new Object();
//
//        // Act & Assert
//        assertThrows(IllegalArgumentException.class, () -> {
//            notificationService.create(invalidDTO);
//        });
//
//        verify(userRepository, never()).findById(any(UUID.class));
//    }
//
//    @Test
//    void create_MultipleDevices_CreatesNotificationForEach() {
//        // Arrange
//        Device device2 = mockDevice(testUser);
//        mockUserRepository(testUser);
//        when(deviceRepository.findByUserId(testUser.getId())).thenReturn(List.of(testDevice, device2));
//        when(notificationMapper.toEntity(any(CreateNotificationDTO.class))).thenReturn(testNotification);
//        when(notificationRepository.saveAll(anyList())).thenReturn(List.of(testNotification, testNotification));
//        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDTO);
//        doNothing().when(webSocketController).pushNotification(any(NotificationDTO.class));
//
//        // Act
//        NotificationDTO result = notificationService.create(createNotificationDTO);
//
//        // Assert
//        assertNotNull(result);
//        verify(deviceRepository, times(1)).findByUserId(testUser.getId());
//        verify(notificationRepository, times(1)).saveAll(argThat(notifications -> {
//            List<Notification> list = (List<Notification>) notifications;
//            return list.size() == 2;
//        }));
//    }
//
//    @Test
//    void create_SaveFails_ThrowsRuntimeException() {
//        // Arrange
//        mockUserRepository(testUser);
//        when(deviceRepository.findByUserId(testUser.getId())).thenReturn(List.of(testDevice));
//        when(notificationMapper.toEntity(any(CreateNotificationDTO.class))).thenReturn(testNotification);
//        when(notificationRepository.saveAll(anyList())).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            notificationService.create(createNotificationDTO);
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to create notification"));
//    }
//
//    // ==================== GET USER NOTIFICATIONS TESTS ====================
//
//    @Test
//    void getUserNotifications_Success() {
//        // Arrange
//        PageFilter pageFilter = new PageFilter();
//        pageFilter.setPage(1);
//        pageFilter.setLimit(10);
//        pageFilter.setSortBy("createdAt");
//        pageFilter.setSortOrder("DESC");
//
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Notification> notificationPage = new PageImpl<>(List.of(testNotification));
//
//        when(notificationRepository.findAll(any(Pageable.class))).thenReturn(notificationPage);
//        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDTO);
//
//        // Act
//        Page<NotificationDTO> result = notificationService.getUserNotifications(testUser.getId(), pageFilter);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals(notificationDTO.getId(), result.getContent().get(0).getId());
//
//        verify(notificationRepository, times(1)).findAll(any(Pageable.class));
//    }
//
//    @Test
//    void getUserNotifications_EmptyResult() {
//        // Arrange
//        PageFilter pageFilter = new PageFilter();
//        pageFilter.setPage(1);
//        pageFilter.setLimit(10);
//        pageFilter.setSortBy("createdAt");
//        pageFilter.setSortOrder("DESC");
//
//        Page<Notification> emptyPage = new PageImpl<>(List.of());
//
//        when(notificationRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
//
//        // Act
//        Page<NotificationDTO> result = notificationService.getUserNotifications(testUser.getId(), pageFilter);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(0, result.getTotalElements());
//        assertTrue(result.getContent().isEmpty());
//    }
//
//    @Test
//    void getUserNotifications_RepositoryFails_ThrowsRuntimeException() {
//        // Arrange
//        PageFilter pageFilter = new PageFilter();
//        pageFilter.setPage(1);
//        pageFilter.setLimit(10);
//        pageFilter.setSortBy("createdAt");
//        pageFilter.setSortOrder("DESC");
//
//        when(notificationRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            notificationService.getUserNotifications(testUser.getId(), pageFilter);
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to get User Notification"));
//    }
//
//    // ==================== MARK AS READ TESTS ====================
//
//    @Test
//    void markAsRead_Success() {
//        // Arrange
//        when(notificationRepository.findById(testNotification.getId())).thenReturn(Optional.of(testNotification));
//        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
//        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDTO);
//
//        // Act
//        NotificationDTO result = notificationService.markAsRead(testNotification.getId());
//
//        // Assert
//        assertNotNull(result);
//        verify(notificationRepository, times(1)).findById(testNotification.getId());
//        verify(notificationRepository, times(1)).save(any(Notification.class));
//    }
//
//    @Test
//    void markAsRead_NotificationNotFound_ThrowsException() {
//        // Arrange
//        UUID notificationId = randomUUID();
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResponseStatusException.class, () -> {
//            notificationService.markAsRead(notificationId);
//        });
//
//        verify(notificationRepository, times(1)).findById(notificationId);
//        verify(notificationRepository, never()).save(any(Notification.class));
//    }
//
//    @Test
//    void markAsRead_SaveFails_ThrowsRuntimeException() {
//        // Arrange
//        mockNotificationRepository(testNotification);
//        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            notificationService.markAsRead(testNotification.getId());
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to mark As Read"));
//    }
//
//    // ==================== MARK AS DELIVERED TESTS ====================
//
//    @Test
//    void markAsDelivered_Success() {
//        // Arrange
//        mockNotificationRepository(testNotification);
//        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
//        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDTO);
//
//        // Act
//        NotificationDTO result = notificationService.markAsDelivered(testNotification.getId());
//
//        // Assert
//        assertNotNull(result);
//        verify(notificationRepository, times(1)).findById(testNotification.getId());
//        verify(notificationRepository, times(1)).save(any(Notification.class));
//        verify(testNotification).setIsDelivered(true);
//        verify(testNotification).setDeliveredAt(any(LocalDateTime.class));
//    }
//
//    @Test
//    void markAsDelivered_NotificationNotFound_ThrowsException() {
//        // Arrange
//        UUID notificationId = randomUUID();
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResponseStatusException.class, () -> {
//            notificationService.markAsDelivered(notificationId);
//        });
//
//        verify(notificationRepository, times(1)).findById(notificationId);
//        verify(notificationRepository, never()).save(any(Notification.class));
//    }
//
//    @Test
//    void markAsDelivered_SaveFails_ThrowsRuntimeException() {
//        // Arrange
//        mockNotificationRepository(testNotification);
//        when(notificationRepository.save(any(Notification.class))).thenThrow(new RuntimeException("Database error"));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            notificationService.markAsDelivered(testNotification.getId());
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to mark As Delivered"));
//    }
//
//    // ==================== DELETE TESTS ====================
//
//    @Test
//    void deleteById_Success() {
//        // Arrange
//        mockNotificationRepository(testNotification);
//        doNothing().when(notificationRepository).delete(any(Notification.class));
//
//        // Act
//        notificationService.deleteById(testNotification.getId());
//
//        // Assert
//        verify(notificationRepository, times(1)).findById(testNotification.getId());
//        verify(notificationRepository, times(1)).delete(testNotification);
//    }
//
//    @Test
//    void deleteById_NotificationNotFound_ThrowsException() {
//        // Arrange
//        UUID notificationId = randomUUID();
//        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(ResponseStatusException.class, () -> {
//            notificationService.deleteById(notificationId);
//        });
//
//        verify(notificationRepository, times(1)).findById(notificationId);
//        verify(notificationRepository, never()).delete(any(Notification.class));
//    }
//
//    @Test
//    void deleteById_DeleteFails_ThrowsRuntimeException() {
//        // Arrange
//        mockNotificationRepository(testNotification);
//        doThrow(new RuntimeException("Database error")).when(notificationRepository).delete(any(Notification.class));
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            notificationService.deleteById(testNotification.getId());
//        });
//
//        assertTrue(exception.getMessage().contains("Failed to delete Notification"));
//    }
//
//    // ==================== UNIMPLEMENTED METHODS TESTS ====================
//
//    @Test
//    void findAll_ThrowsUnsupportedOperationException() {
//        // Act & Assert
//        assertThrows(UnsupportedOperationException.class, () -> {
//            notificationService.findAll();
//        });
//    }
//
//    @Test
//    void findById_ThrowsUnsupportedOperationException() {
//        // Act & Assert
//        assertThrows(UnsupportedOperationException.class, () -> {
//            notificationService.findById(randomUUID());
//        });
//    }
//
//    @Test
//    void update_ThrowsUnsupportedOperationException() {
//        // Act & Assert
//        assertThrows(UnsupportedOperationException.class, () -> {
//            notificationService.update(randomUUID(), new Object());
//        });
//    }
//}