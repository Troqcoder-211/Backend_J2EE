package j2ee.ourteam.services.mail;

import j2ee.ourteam.entities.PasswordResetOtp;

public interface IMailService{
    void sendTextMail(String to, PasswordResetOtp otp);
}