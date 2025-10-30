package j2ee.ourteam.services.otp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.PasswordResetOtp;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.repositories.PasswordResetOtpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements IOtpService{
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtpCode() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Override
    public PasswordResetOtp generateOtp(User user){
        String code = generateOtpCode();
        PasswordResetOtp otp = PasswordResetOtp.builder()
                    .user(user)
                    .otpCode(code)
                    .expiresAt(LocalDateTime.now().plusMinutes(15))
                    .build();
        return passwordResetOtpRepository.save(otp);
    }

    @Transactional
    @Override
    public boolean verifyOtp(UUID userId, String otpCode){
        Optional<PasswordResetOtp> optOtp = passwordResetOtpRepository.findValidOtp(userId, otpCode, LocalDateTime.now());
        if (optOtp.isEmpty()) {
            return false;
        }
        PasswordResetOtp passwordResetOtp = optOtp.get();
        passwordResetOtp.setUsed(true);
        passwordResetOtpRepository.save(passwordResetOtp);
        return true;
    }

}
