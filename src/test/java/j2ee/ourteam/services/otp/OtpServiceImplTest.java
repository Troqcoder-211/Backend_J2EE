package j2ee.ourteam.services.otp;

import j2ee.ourteam.entities.PasswordResetOtp;
import j2ee.ourteam.entities.User;
import j2ee.ourteam.repositories.PasswordResetOtpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceImplTest {

    private PasswordResetOtpRepository passwordResetOtpRepository;
    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        passwordResetOtpRepository = mock(PasswordResetOtpRepository.class);
        otpService = new OtpServiceImpl(passwordResetOtpRepository);
    }

    @Test
    void generateOtpCode_shouldReturn6DigitString() {
        String otp = otpService.generateOtpCode();
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }

    @Test
    void generateOtp_shouldSaveOtpForUser() {
        User user = new User();
        user.setId(UUID.randomUUID());

        ArgumentCaptor<PasswordResetOtp> captor = ArgumentCaptor.forClass(PasswordResetOtp.class);
        when(passwordResetOtpRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PasswordResetOtp otp = otpService.generateOtp(user);

        verify(passwordResetOtpRepository).save(captor.capture());
        PasswordResetOtp savedOtp = captor.getValue();

        assertEquals(user, savedOtp.getUser());
        assertNotNull(savedOtp.getOtpCode());
        assertTrue(savedOtp.getExpiresAt().isAfter(LocalDateTime.now()));
        assertEquals(savedOtp, otp);
    }

    @Test
    void verifyOtp_shouldReturnFalseIfOtpNotFound() {
        UUID userId = UUID.randomUUID();
        String otpCode = "123456";

        when(passwordResetOtpRepository.findValidOtp(eq(userId), eq(otpCode), any()))
                .thenReturn(Optional.empty());

        boolean result = otpService.verifyOtp(userId, otpCode);
        assertFalse(result);
        verify(passwordResetOtpRepository, never()).save(any());
    }

    @Test
    void verifyOtp_shouldReturnTrueAndMarkUsedIfOtpFound() {
        UUID userId = UUID.randomUUID();
        String otpCode = "654321";

        PasswordResetOtp otp = PasswordResetOtp.builder()
                .user(new User() {{ setId(userId); }})
                .otpCode(otpCode)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(passwordResetOtpRepository.findValidOtp(eq(userId), eq(otpCode), any()))
                .thenReturn(Optional.of(otp));

        when(passwordResetOtpRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = otpService.verifyOtp(userId, otpCode);

        assertTrue(result);
        assertTrue(otp.isUsed());
        verify(passwordResetOtpRepository).save(otp);
    }
}
