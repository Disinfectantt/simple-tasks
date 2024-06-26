package xyz.cringe.simpletasks.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TeamUniqValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTeam {
    String message() default "Team already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
