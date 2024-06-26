package xyz.cringe.simpletasks.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import xyz.cringe.simpletasks.service.TaskStatusService;

public class StatusValidator implements ConstraintValidator<StatusValid, Long> {
    private final TaskStatusService taskStatusService;

    public StatusValidator(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
    }

    @Override
    public boolean isValid(Long statusId,
                           ConstraintValidatorContext cxt) {
        return statusId != null && taskStatusService.getTaskStatus(statusId) != null;
    }
}
