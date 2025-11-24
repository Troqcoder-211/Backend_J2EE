package j2ee.ourteam.services.otp;

import j2ee.ourteam.BaseTest;
import j2ee.ourteam.repositories.PasswordResetOtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class OtpCleanupServiceTest extends BaseTest {

    private PasswordResetOtpRepository otpRepository;
    private OtpCleanupService otpCleanupService;

    @BeforeEach
    void setUp() {
        otpRepository = mock(PasswordResetOtpRepository.class);
        otpCleanupService = new OtpCleanupService(otpRepository);
    }

    @Test
    void cleanupExpiredOtps_shouldCallRepositoryWithCurrentTime() {
        // Gọi method
        otpCleanupService.cleanupExpiredOtps();

        // Kiểm tra repository có được gọi với thời điểm hiện tại
        verify(otpRepository, times(1)).deleteExpiredOrUsedOtps(any(LocalDateTime.class));
    }
}
