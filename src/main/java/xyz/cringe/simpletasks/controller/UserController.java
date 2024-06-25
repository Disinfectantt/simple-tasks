package xyz.cringe.simpletasks.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.cringe.simpletasks.annotations.HxRequestOnly;
import xyz.cringe.simpletasks.dto.UserDto;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.service.RoleService;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TeamService;
import xyz.cringe.simpletasks.service.UserService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@PreAuthorize("hasRole('ADMIN')")
@Validated
@RequestMapping("/users")
@Controller
public class UserController {
    private final UserService userService;
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final SseEmitterService sseEmitterService;
    private final TeamService teamService;
    private final RoleService roleService;

    public UserController(UserService userService, SseEmitterService sseEmitterService, TeamService teamService, RoleService roleService) {
        this.userService = userService;
        this.sseEmitterService = sseEmitterService;
        this.teamService = teamService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String all(Model model, HttpServletRequest request) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        String hxRequestHeader = request.getHeader("HX-Request");
        if (hxRequestHeader == null) {
            model.addAttribute("currentPage", "/tasks/");
            return "index";
        }
        return "pages/all_users";
    }

    @HxRequestOnly
    @GetMapping("/user_form")
    public String teamsForm(UserDto userDto, Model model) {
        model.addAttribute("userDto", userDto);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("roles", roleService.findAll());
        return "layouts/forms/add-new-user";
    }

    @HxRequestOnly
    @GetMapping("/user_form/{id}")
    public String teamsFormById(@PathVariable Long id, Model model) {
        UserDto userDto = userService.findById(id);
        model.addAttribute("userDto", userDto);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("teams", teamService.getAllTeams());
        return "layouts/forms/add-new-user";
    }

    @PostMapping("/")
    public String add(@Valid @ModelAttribute("userDto") UserDto userDto,
                      BindingResult result, Model model,
                      HttpServletResponse response) {
        return processRequest(userDto, result, model, null, response);
    }

    @PostMapping("/{id}")
    public String update(@Valid @ModelAttribute("userDto") UserDto userDto,
                         BindingResult result, Model model,
                         @PathVariable Long id,
                         HttpServletResponse response) {
        return processRequest(userDto, result, model, id, response);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        sseEmitterService.sendEvent(
                logger, sseEmitters,
                sseEmitterService.buildData("layouts/users",
                        userService.findAll(), "tasks"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sseUsers")
    public SseEmitter sse(@AuthenticationPrincipal UserDetails userDetails) {
        return sseEmitterService.createSseEmitter(sseEmitters, userDetails.getUsername(), logger);
    }

    private String processRequest(UserDto userDto, BindingResult result, Model model, Long id,
                                  HttpServletResponse response) {
        if (result.hasErrors()) {
            model.addAttribute("userDto", userDto);
        } else {
            if (id == null) {
                userService.save(userDto);
            } else {
                userService.update(id, userDto);
            }
            response.addHeader("Hx-Trigger", "closeModal");
            sseEmitterService.sendEvent(
                    logger, sseEmitters,
                    sseEmitterService.buildData("layouts/users",
                            userService.findAll(), "users"));
        }
        return "layouts/forms/add-new-user";
    }
}
