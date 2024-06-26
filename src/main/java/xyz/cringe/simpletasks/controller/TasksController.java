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
import xyz.cringe.simpletasks.dto.TaskDto;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TaskService;
import xyz.cringe.simpletasks.service.TaskStatusService;
import xyz.cringe.simpletasks.service.TeamService;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Validated
@RequestMapping("/tasks")
@Controller
public class TasksController {
    private final TaskService taskService;
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(TasksController.class);
    private final SseEmitterService sseEmitterService;
    private final TeamService teamService;
    private final TaskStatusService taskStatusService;

    public TasksController(TaskService taskService, SseEmitterService sseEmitterService, TeamService teamService, TaskStatusService taskStatusService) {
        this.taskService = taskService;
        this.sseEmitterService = sseEmitterService;
        this.teamService = teamService;
        this.taskStatusService = taskStatusService;
    }

    @GetMapping("/")
    public String all(Model model, HttpServletRequest request) {
        List<Task> tasks = taskService.findAll();
        model.addAttribute("tasks", tasks);
        String hxRequestHeader = request.getHeader("HX-Request");
        if (hxRequestHeader == null) {
            model.addAttribute("currentPage", "/tasks/");
            return "index";
        }
        return "pages/all_tasks";
    }

    @PreAuthorize("hasRole('EDITOR')")
    @HxRequestOnly
    @GetMapping("/task_form")
    public String teamsForm(TaskDto taskDto, Model model) {
        taskDto.setDifficulty(0);
        taskDto.setPriority(0);
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("statuses", taskStatusService.getAllTaskStatus());
        return "layouts/forms/add-new-task";
    }

    @PreAuthorize("hasRole('EDITOR')")
    @HxRequestOnly
    @GetMapping("/tasks_form/{id}")
    public String teamsFormById(@PathVariable Long id, Model model) {
        TaskDto taskDto = taskService.getTask(id);
        model.addAttribute("taskDto", taskDto);
        model.addAttribute("teams", teamService.getAllTeams());
        model.addAttribute("statuses", taskStatusService.getAllTaskStatus());
        return "layouts/forms/add-new-task";
    }

    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping("/")
    public String add(@Valid @ModelAttribute("taskDto") TaskDto taskDto,
                      BindingResult result, Model model,
                      HttpServletResponse response) {
        return processRequest(taskDto, result, model, null, response);
    }

    @PreAuthorize("hasRole('EDITOR')")
    @PostMapping("/{id}")
    public String update(@Valid @ModelAttribute("taskDto") TaskDto taskDto,
                         BindingResult result, Model model,
                         @PathVariable Long id,
                         HttpServletResponse response) {
        return processRequest(taskDto, result, model, id, response);
    }

    @PreAuthorize("hasRole('EDITOR')")
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        sseEmitterService.sendEvent(
                logger, sseEmitters,
                sseEmitterService.buildData("layouts/tasks",
                        taskService.findAll(), "tasks"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sseTasks")
    public SseEmitter sse(@AuthenticationPrincipal UserDetails userDetails) {
        return sseEmitterService.createSseEmitter(sseEmitters, userDetails.getUsername(), logger);
    }

    @PostMapping("/take/{id}")
    @ResponseBody
    public ResponseEntity<Void> take(@PathVariable Long id, Principal principal) {
        taskService.updateCurrentWorker(id, principal.getName());
        sseEmitterService.sendEvent(
                logger, sseEmitters,
                sseEmitterService.buildData("layouts/tasks",
                        taskService.findAll(), "tasks"));
        return ResponseEntity.ok().build();
    }

    private String processRequest(TaskDto taskDto, BindingResult result, Model model, Long id,
                                  HttpServletResponse response) {
        if (result.hasErrors()) {
            model.addAttribute("taskDto", taskDto);
        } else {
            if (id == null) {
                taskService.create(taskDto);
            } else {
                taskService.update(id, taskDto);
            }
            response.addHeader("Hx-Trigger", "closeModal");
            sseEmitterService.sendEvent(
                    logger, sseEmitters,
                    sseEmitterService.buildData("layouts/tasks",
                            taskService.findAll(), "tasks"));
        }
        return "layouts/forms/add-new-task";
    }

}
