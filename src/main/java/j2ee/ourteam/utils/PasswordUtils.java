package j2ee.ourteam.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {
  private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  // Mã hóa mật khẩu
  public static String encodePassword(String password) {
    return encoder.encode(password);
  }

  // Kiểm tra mật khẩu có khớp với hash không
  public static boolean checkPassword(String rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
