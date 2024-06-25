package xyz.cringe.simpletasks.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import xyz.cringe.simpletasks.service.TeamService;

public class TeamValidator implements ConstraintValidator<TeamValid, Long> {

    private final TeamService teamService;

    public TeamValidator(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public boolean isValid(Long teamId,
                           ConstraintValidatorContext cxt) {
        return teamId != null && teamService.getTeamById(teamId) != null;
    }
}
