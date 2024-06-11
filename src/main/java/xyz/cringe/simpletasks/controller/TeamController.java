package xyz.cringe.simpletasks.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.repo.TaskRepo;
import xyz.cringe.simpletasks.repo.TeamRepo;
import xyz.cringe.simpletasks.service.TeamService;

import java.util.List;

@RequestMapping("/teams")
@Controller
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/")
    public String all(Model model) {
        List<Team> teams = teamService.getAllTeams();
        model.addAttribute("teams", teams);
        return "pages/all_teams";
    }

    @GetMapping("/teams_form")
    public String teamsForm(TeamDto teamDto) {
        teamDto.setEnabled(true);
        return "layouts/forms/add-new-team";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("teamDto") TeamDto teamDto, BindingResult result, Model model) {
        model.addAttribute("teamDto", teamDto);
        if (result.hasErrors()) {
//            model.addAttribute("errors", result.getAllErrors());
            return "layouts/forms/add-new-team";
        }
        teamService.createTeam(teamDto);
        return "layouts/forms/add-new-team";
    }
}
