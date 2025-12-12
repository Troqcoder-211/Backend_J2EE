package j2ee.ourteam.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUUIDValidator.class) // trỏ đến class validator
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUUID {
  String message() default "Invalid UUID format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
