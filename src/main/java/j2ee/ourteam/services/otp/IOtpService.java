package j2ee.ourteam.services.otp;

import java.util.UUID;

import j2ee.ourteam.entities.PasswordResetOtp;
import j2ee.ourteam.entities.User;

public interface IOtpService {
    String generateOtpCode();
    PasswordResetOtp generateOtp(User user);
    boolean verifyOtp(UUID userId, String otpCode);
}
