package j2ee.ourteam.services.otp;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import j2ee.ourteam.repositories.PasswordResetOtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpCleanupService {
    private final PasswordResetOtpRepository otpRepository;

    @Scheduled(fixedDelay = 3600000) // Mỗi 1 tiếng
    @Transactional
    public void cleanupExpiredOtps() {
        int deleted = otpRepository.deleteExpiredOrUsedOtps(LocalDateTime.now());
        if (deleted > 0) {
            System.out.println("Deleted " + deleted + " expired/used OTPs");
        }
    }
}
