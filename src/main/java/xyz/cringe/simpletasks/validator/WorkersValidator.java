package xyz.cringe.simpletasks.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import xyz.cringe.simpletasks.service.UserService;

import java.util.Set;

public class WorkersValidator implements ConstraintValidator<WorkersValid, Set<Long>> {
    private final UserService userService;

    public WorkersValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(Set<Long> workers,
                           ConstraintValidatorContext cxt) {
        return workers != null && workers.stream()
                .allMatch(workerId -> userService.findById(workerId) != null);
    }
}
