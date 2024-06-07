package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.repo.TaskStatusRepo;

import java.util.List;

@Service
public class TaskStatusService {
    private final TaskStatusRepo taskStatusRepo;

    public TaskStatusService(TaskStatusRepo taskStatusRepo) {
        this.taskStatusRepo = taskStatusRepo;
    }

    public TaskStatus getTaskStatus(Long taskId) {
        return taskStatusRepo.findById(taskId).orElse(null);
    }

    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusRepo.findAll();
    }

    public void createTaskStatus(TaskStatus taskStatus) {
        taskStatusRepo.save(taskStatus);
    }

}
