package xyz.cringe.simpletasks.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StatusValid {
    String message() default "Status is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
