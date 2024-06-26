package xyz.cringe.simpletasks.ValidatorTest;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.service.TeamService;
import xyz.cringe.simpletasks.validator.TeamUniqValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TeamUniqValidatorTest {
    @Mock
    private TeamService teamService;

    @Mock
    private ConstraintValidatorContext context;

    private TeamUniqValidator teamUniqValidator;

    @BeforeEach
    void setUp() {
        teamUniqValidator = new TeamUniqValidator(teamService);
    }

    @Test
    void isValid_ShouldReturnTrue_WhenTeamNameDoesNotExist() {
        String teamName = "New Team";
        when(teamService.getTeamByName(teamName)).thenReturn(null);

        boolean result = teamUniqValidator.isValid(teamName, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenTeamNameAlreadyExists() {
        String teamName = "Existing Team";
        TeamDto existingTeam = new TeamDto();
        when(teamService.getTeamByName(teamName)).thenReturn(existingTeam);

        boolean result = teamUniqValidator.isValid(teamName, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnFalse_WhenTeamNameIsNull() {
        boolean result = teamUniqValidator.isValid(null, context);

        assertThat(result).isFalse();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenTeamNameIsEmptyString() {
        String teamName = "";
        when(teamService.getTeamByName(teamName)).thenReturn(null);

        boolean result = teamUniqValidator.isValid(teamName, context);

        assertThat(result).isTrue();
    }

    @Test
    void isValid_ShouldReturnTrue_WhenTeamNameIsWhitespace() {
        String teamName = "   ";
        when(teamService.getTeamByName(teamName)).thenReturn(null);

        boolean result = teamUniqValidator.isValid(teamName, context);

        assertThat(result).isTrue();
    }
}
