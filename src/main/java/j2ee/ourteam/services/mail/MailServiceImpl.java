package j2ee.ourteam.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import j2ee.ourteam.entities.PasswordResetOtp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String fromEmail;

    @Override
    public void sendTextMail(String to, PasswordResetOtp otp) {
        try {
            // 1. Tạo MimeMessage để hỗ trợ HTML
            MimeMessage message = mailSender.createMimeMessage();

            // 2. Sử dụng Helper: true = multipart (có đính kèm hoặc html), "UTF-8" để không
            // lỗi font tiếng Việt
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Xác thực quên mật khẩu - Mã OTP");

            // 3. Lấy nội dung HTML từ hàm bên dưới và chèn mã OTP vào
            // Giả sử OTP hết hạn sau 5 phút (bạn có thể lấy từ config)
            String htmlContent = getOtpEmailTemplate(otp.getOtpCode(), 5);

            // 4. Set nội dung là HTML (param thứ 2 là boolean html = true)
            helper.setText(htmlContent, true);

            // 5. Gửi mail
            mailSender.send(message);
            log.info("Email OTP đã được gửi thành công đến: {}", to);

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email: ", e);
            throw new RuntimeException("Không thể gửi email xác thực, vui lòng thử lại sau.");
        }
    }

    /**
     * Tạo template HTML cho email
     * Sử dụng Java Text Block (Java 15+) để viết HTML dễ nhìn hơn
     */
    private String getOtpEmailTemplate(String otpCode, int expireMinutes) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 30px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05); overflow: hidden; }
                        .header { background-color: #007bff; color: #ffffff; padding: 20px; text-align: center; }
                        .header h1 { margin: 0; font-size: 24px; }
                        .content { padding: 30px 20px; text-align: center; color: #333333; }
                        .otp-box {
                            font-size: 32px;
                            font-weight: bold;
                            color: #007bff;
                            letter-spacing: 5px;
                            margin: 20px auto;
                            padding: 15px;
                            border: 2px dashed #007bff;
                            background-color: #f0f8ff;
                            display: inline-block;
                            border-radius: 5px;
                        }
                        .warning { font-size: 13px; color: #d9534f; margin-top: 20px; }
                        .footer { background-color: #f4f4f4; padding: 15px; text-align: center; font-size: 12px; color: #777777; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Chat App</h1>
                        </div>
                        <div class="content">
                            <p>Xin chào,</p>
                            <p>Bạn (hoặc ai đó) đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.</p>
                            <p>Đây là mã xác thực OTP của bạn:</p>

                            <div class="otp-box">%s</div>

                            <p>Mã này sẽ hết hạn trong vòng <strong>%d phút</strong>.</p>
                            <div class="warning">
                                <p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này. Tuyệt đối không chia sẻ mã này cho bất kỳ ai.</p>
                            </div>
                        </div>
                        <div class="footer">
                            <p>&copy; 2025 OurTeam Project. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(otpCode, expireMinutes);
    }
}