package xyz.cringe.simpletasks.ValidatorTest;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.TaskStatusDto;
import xyz.cringe.simpletasks.service.TaskStatusService;
import xyz.cringe.simpletasks.validator.StatusValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatusValidatorTest {
    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private ConstraintValidatorContext context;

    private StatusValidator statusValidator;

    @BeforeEach
    void setUp() {
        statusValidator = new StatusValidator(taskStatusService);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenStatusExists() {
        Long statusId = 1L;
        TaskStatusDto mockStatus = new TaskStatusDto(); // Assume this class exists
        when(taskStatusService.getTaskStatus(statusId)).thenReturn(mockStatus);

        boolean result = statusValidator.isValid(statusId, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenStatusDoesNotExist() {
        Long statusId = 1L;
        when(taskStatusService.getTaskStatus(statusId)).thenReturn(null);

        boolean result = statusValidator.isValid(statusId, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenStatusIdIsNull() {
        boolean result = statusValidator.isValid(null, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenStatusIdIsNegative() {
        Long statusId = -1L;

        boolean result = statusValidator.isValid(statusId, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenStatusIdIsZero() {
        Long statusId = 0L;

        boolean result = statusValidator.isValid(statusId, context);

        assertThat(result).isFalse();
    }
}
