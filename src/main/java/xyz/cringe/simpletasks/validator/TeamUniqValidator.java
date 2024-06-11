package xyz.cringe.simpletasks.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import xyz.cringe.simpletasks.service.TeamService;

public class TeamUniqValidator implements ConstraintValidator<UniqueTeam, String> {

    private final TeamService teamService;

    TeamUniqValidator(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public void initialize(UniqueTeam contactNumber) {
    }

    @Override
    public boolean isValid(String teamNameField,
                           ConstraintValidatorContext cxt) {
        return teamNameField != null && teamService.getTeamByName(teamNameField) == null;
    }
}
