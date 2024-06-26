package xyz.cringe.simpletasks.ValidatorTest;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.service.TeamService;
import xyz.cringe.simpletasks.validator.TeamValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamValidatorTest {
    @Mock
    private TeamService teamService;

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private TeamValidator teamValidator;

    @BeforeEach
    void setUp() {
        teamValidator = new TeamValidator(teamService);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenTeamIdExists() {
        Long teamId = 1L;
        TeamDto existingTeam = new TeamDto();
        when(teamService.getTeamById(teamId)).thenReturn(existingTeam);

        boolean result = teamValidator.isValid(teamId, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenTeamIdDoesNotExist() {
        Long teamId = 1L;
        when(teamService.getTeamById(teamId)).thenReturn(null);

        boolean result = teamValidator.isValid(teamId, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenTeamIdIsNull() {
        boolean result = teamValidator.isValid(null, context);

        assertThat(result).isFalse();
    }

}
