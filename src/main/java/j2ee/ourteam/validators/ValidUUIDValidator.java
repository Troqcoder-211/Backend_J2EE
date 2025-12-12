package j2ee.ourteam.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

public class ValidUUIDValidator implements ConstraintValidator<ValidUUID, UUID> {

  @Override
  public boolean isValid(UUID value, ConstraintValidatorContext context) {
    // Cho phép null (nếu không muốn thì đổi lại return false)
    if (value == null)
      return true;

    try {
      // Thử parse lại để kiểm tra UUID hợp lệ
      UUID.fromString(value.toString());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
