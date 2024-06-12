package xyz.cringe.simpletasks.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TeamService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@RequestMapping("/teams")
@Controller
public class TeamController {
    private final TeamService teamService;
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(TeamController.class);
    private final SseEmitterService sseEmitterService;

    public TeamController(TeamService teamService, SseEmitterService sseEmitterService) {
        this.teamService = teamService;
        this.sseEmitterService = sseEmitterService;
    }

    @GetMapping("/")
    public String all(Model model) {
        //TODO HX-Request header
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
    public String add(@Valid @ModelAttribute("teamDto") TeamDto teamDto,
                      BindingResult result, Model model,
                      @AuthenticationPrincipal UserDetails userDetails) {
        if (result.hasErrors()) {
            model.addAttribute("teamDto", teamDto);
        } else {
            teamService.createTeam(teamDto);
            sseEmitterService.sendEvent(
                    logger, sseEmitters,
                    userDetails.getUsername(),
                    sseEmitterService.buildData("pages/all_teams",
                            teamService.getAllTeams(), "teams"));
            teamDto.setEnabled(true);
            teamDto.setName(null);
        }
        return "layouts/forms/add-new-team";
    }

    @GetMapping("/sseTeams")
    public SseEmitter sse(@AuthenticationPrincipal UserDetails userDetails) {
        return sseEmitterService.createSseEmitter(sseEmitters, userDetails.getUsername(), logger);
    }

}
