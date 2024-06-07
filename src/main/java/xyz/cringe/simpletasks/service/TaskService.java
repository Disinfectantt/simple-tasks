package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.repo.TaskRepo;

@Service
public class TaskService {
    private final TaskRepo taskRepo;

    public TaskService(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public void create(Task task) {
        taskRepo.save(task);
    }

    public Task find(Long id) {
        return taskRepo.findById(id).orElse(null);
    }
}
