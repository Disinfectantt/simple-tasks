package xyz.cringe.simpletasks.ValidatorTest;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.UserDto;
import xyz.cringe.simpletasks.service.UserService;
import xyz.cringe.simpletasks.validator.WorkersValidator;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkersValidatorTest {
    @Mock
    private UserService userService;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private WorkersValidator workersValidator;

    @BeforeEach
    void setUp() {
        workersValidator = new WorkersValidator(userService);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenAllWorkerIdsExist() {
        Set<Long> workerIds = new HashSet<>();
        workerIds.add(1L);
        workerIds.add(2L);

        when(userService.findById(1L)).thenReturn(new UserDto());
        when(userService.findById(2L)).thenReturn(new UserDto());

        boolean result = workersValidator.isValid(workerIds, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenAnyWorkerIdDoesNotExist() {
        Set<Long> workerIds = new HashSet<>();
        workerIds.add(1L);
        workerIds.add(2L);

        when(userService.findById(1L)).thenReturn(new UserDto());
        when(userService.findById(2L)).thenReturn(null);

        boolean result = workersValidator.isValid(workerIds, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenWorkerIdsIsNull() {
        boolean result = workersValidator.isValid(null, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenWorkerIdsIsEmpty() {
        Set<Long> workerIds = new HashSet<>();

        boolean result = workersValidator.isValid(workerIds, context);

        assertThat(result).isTrue();
    }
}
