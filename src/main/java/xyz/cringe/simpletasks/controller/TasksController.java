package xyz.cringe.simpletasks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
        if(tasks.isEmpty()) {
            return "empty";
        }
        model.addAttribute("tasks", tasks);
        return "pages/all_tasks";
    }

}
