package j2ee.ourteam.repositories;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import j2ee.ourteam.entities.PasswordResetOtp;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID>{

    @Query("""
        SELECT o FROM PasswordResetOtp o
        WHERE o.user.id = :userId
          AND o.otpCode = :otpCode
          AND o.isUsed = false
          AND o.expiresAt > :now
    """)
    Optional<PasswordResetOtp> findValidOtp(
            @Param("userId") UUID userId,
            @Param("otpCode") String otpCode,
            @Param("now") LocalDateTime now
    );

    @Modifying
    @Query("""
        DELETE FROM PasswordResetOtp o
        WHERE o.expiresAt < :now OR o.isUsed = true
    """)
    void deleteExpiredOrUsedOtps(@Param("now") LocalDateTime now);
}