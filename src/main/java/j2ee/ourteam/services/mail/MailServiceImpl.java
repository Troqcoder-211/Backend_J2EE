package j2ee.ourteam.services.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.PasswordResetOtp;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService{

    private final JavaMailSender mailSender;

    @Value("${app/mail/from}")
    private String fromEmail;

    @Override
    public void sendTextMail(String to, PasswordResetOtp otp) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromEmail);
        mailMessage.setTo(to);
        mailMessage.setSubject("OTP");
        mailMessage.setText(otp.getOtpCode());
        mailSender.send(mailMessage);
    }
    
}
