package xyz.cringe.simpletasks.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TeamValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface TeamValid {
    String message() default "Team is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
