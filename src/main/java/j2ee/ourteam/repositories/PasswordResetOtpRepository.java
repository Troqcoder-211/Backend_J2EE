package j2ee.ourteam.repositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import j2ee.ourteam.entities.PasswordResetOtp;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID>{

    @Modifying
    @Query("""
        SELECT o FROM password_reset_otps o
        WHERE o.user_id = :userId
          AND o.otp_code = :otpCode
          AND o.is_used = false
          AND o.expires_at > :now
        """)
    Optional<PasswordResetOtp> findValidOtp(UUID userId, String otpCode, LocalDateTime now);

    @Modifying
    @Query("DELETE FROM password_reset_otps o WHERE o.expires_at < :now OR o.is_used = true")
    int deleteExpiredOrUsedOtps(LocalDateTime now);
}