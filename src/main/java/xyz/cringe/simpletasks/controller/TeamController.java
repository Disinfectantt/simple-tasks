package xyz.cringe.simpletasks.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.cringe.simpletasks.annotations.HxRequestOnly;
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
    public String all(Model model, HttpServletRequest request) {
        List<Team> teams = teamService.getAllTeams();
        model.addAttribute("teams", teams);
        String hxRequestHeader = request.getHeader("HX-Request");
        if (hxRequestHeader == null) {
            model.addAttribute("currentPage", "/teams/");
            return "index";
        }
        return "pages/all_teams";
    }

    @HxRequestOnly
    @GetMapping("/teams_form")
    public String teamsForm(TeamDto teamDto) {
        teamDto.setEnabled(true);
        return "layouts/forms/add-new-team";
    }

    @HxRequestOnly
    @GetMapping("/teams_form/{id}")
    public String teamsFormById(@PathVariable Long id, Model model) {
        TeamDto teamDto = teamService.getTeamById(id);
        model.addAttribute("teamDto", teamDto);
        return "layouts/forms/add-new-team";
    }

    @PostMapping("/")
    public String add(@Valid @ModelAttribute("teamDto") TeamDto teamDto,
                      BindingResult result, Model model) {
        return processRequest(teamDto, result, model, null);
    }

    @PostMapping("/{id}")
    public String update(@Valid @ModelAttribute("teamDto") TeamDto teamDto,
                         BindingResult result, Model model,
                         @PathVariable Long id) {
        return processRequest(teamDto, result, model, id);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teamService.deleteTeamById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sseTeams")
    public SseEmitter sse(@AuthenticationPrincipal UserDetails userDetails) {
        return sseEmitterService.createSseEmitter(sseEmitters, userDetails.getUsername(), logger);
    }

    private String processRequest(TeamDto teamDto, BindingResult result, Model model, Long id) {
        if (result.hasErrors()) {
            model.addAttribute("teamDto", teamDto);
        } else {
            if (id == null) {
                teamService.createTeam(teamDto);
                teamDto.setEnabled(true);
                teamDto.setName(null);
            } else {
                teamService.updateTeam(teamDto, id);
            }
            sseEmitterService.sendEvent(
                    logger, sseEmitters,
                    sseEmitterService.buildData("layouts/teams",
                            teamService.getAllTeams(), "teams"));
        }
        return "layouts/forms/add-new-team";
    }

}
