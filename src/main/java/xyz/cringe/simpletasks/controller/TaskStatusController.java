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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.cringe.simpletasks.annotations.HxRequestOnly;
import xyz.cringe.simpletasks.dto.TaskStatusDto;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TaskStatusService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/tasksStatuses")
@Controller
public class TaskStatusController {
    private final TaskStatusService taskStatusService;
    private final ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(TaskStatusController.class);
    private final SseEmitterService sseEmitterService;

    public TaskStatusController(TaskStatusService taskStatusService, SseEmitterService sseEmitterService) {
        this.taskStatusService = taskStatusService;
        this.sseEmitterService = sseEmitterService;
    }

    @GetMapping("/")
    public String all(Model model, HttpServletRequest request) {
        List<TaskStatus> tasksStatuses = taskStatusService.getAllTaskStatus();
        model.addAttribute("tasksStatuses", tasksStatuses);
        String hxRequestHeader = request.getHeader("HX-Request");
        if (hxRequestHeader == null) {
            model.addAttribute("currentPage", "/tasksStatuses/");
            return "index";
        }
        return "pages/all_taskStatuses";
    }

    @HxRequestOnly
    @GetMapping("/taskStatuses_form")
    public String teamsForm(TaskStatus taskStatusDto, Model model) {
        taskStatusDto.setEnabled(true);
        model.addAttribute("taskStatusDto", taskStatusDto);
        return "layouts/forms/add-new-taskStatus";
    }

    @HxRequestOnly
    @GetMapping("/tasksStatuses_form/{id}")
    public String teamsFormById(@PathVariable Long id, Model model) {
        TaskStatusDto taskStatusDto = taskStatusService.getTaskStatus(id);
        model.addAttribute("taskStatusDto", taskStatusDto);
        return "layouts/forms/add-new-taskStatus";
    }

    @PostMapping("/")
    public String add(@Valid @ModelAttribute("taskStatusDto") TaskStatusDto taskStatusDto,
                      BindingResult result, Model model,
                      HttpServletResponse response) {
        return processRequest(taskStatusDto, result, model, null, response);
    }

    @PostMapping("/{id}")
    public String update(@Valid @ModelAttribute("taskStatusDto") TaskStatusDto taskStatusDto,
                         BindingResult result, Model model,
                         @PathVariable Long id,
                         HttpServletResponse response) {
        return processRequest(taskStatusDto, result, model, id, response);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskStatusService.deleteTaskStatus(id);
        sseEmitterService.sendEvent(
                logger, sseEmitters,
                sseEmitterService.buildData("layouts/tasksStatuses",
                        taskStatusService.getAllTaskStatus(), "tasksStatuses"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sseTasksStatuses")
    public SseEmitter sse(@AuthenticationPrincipal UserDetails userDetails) {
        return sseEmitterService.createSseEmitter(sseEmitters, userDetails.getUsername(), logger);
    }

    private String processRequest(TaskStatusDto taskStatusDto, BindingResult result, Model model, Long id,
                                  HttpServletResponse response) {
        if (result.hasErrors()) {
            model.addAttribute("taskStatusDto", taskStatusDto);
        } else {
            if (id == null) {
                taskStatusService.createTaskStatus(taskStatusDto);
                taskStatusDto.setEnabled(true);
                taskStatusDto.setStatus(null);
            } else {
                taskStatusService.updateTaskStatus(id, taskStatusDto);
            }
            response.addHeader("Hx-Trigger", "closeModal");
            sseEmitterService.sendEvent(
                    logger, sseEmitters,
                    sseEmitterService.buildData("layouts/tasksStatuses",
                            taskStatusService.getAllTaskStatus(), "tasksStatuses"));
        }
        return "layouts/forms/add-new-taskStatus";
    }
}
