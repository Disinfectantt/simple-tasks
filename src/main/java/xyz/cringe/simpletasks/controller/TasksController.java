package xyz.cringe.simpletasks.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.repo.TaskRepo;

import java.util.List;

@Validated
@RequestMapping("/tasks")
@Controller
public class TasksController {
    private final TaskRepo taskRepo;

    public TasksController(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    @GetMapping("/")
    public String all(Model model) {
        List<Task> tasks = taskRepo.findAll();
        model.addAttribute("tasks", tasks);
        return "pages/all_tasks";
    }

    @PostMapping("/add")
    public ResponseEntity<Void> add(@Valid @ModelAttribute("task") Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("task", task);
            model.addAttribute("errors", result.getAllErrors());
            return ResponseEntity.badRequest().build();
        }
        taskRepo.save(task);
        return ResponseEntity.ok().build();
    }

}
