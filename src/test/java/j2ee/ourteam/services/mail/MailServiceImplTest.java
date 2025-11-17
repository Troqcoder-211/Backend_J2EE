package j2ee.ourteam.services.mail;

import j2ee.ourteam.entities.PasswordResetOtp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MailServiceImplTest {

    private JavaMailSender mailSender;
    private MailServiceImpl mailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mailService = new MailServiceImpl(mailSender);
        mailService.fromEmail = "test@example.com"; // set email giả lập
    }

    @Test
    void sendTextMail_ShouldCallMailSenderWithCorrectMessage() {
        // Arrange
        PasswordResetOtp otp = new PasswordResetOtp();
        otp.setOtpCode("123456");

        // Act
        mailService.sendTextMail("user@example.com", otp);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals("test@example.com", sentMessage.getFrom());
        assertEquals("user@example.com", sentMessage.getTo()[0]);
        assertEquals("OTP", sentMessage.getSubject());
        assertEquals("123456", sentMessage.getText());
    }
}
