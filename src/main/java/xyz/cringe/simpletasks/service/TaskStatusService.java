package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.dto.TaskStatusDto;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.repo.TaskStatusRepo;

import java.util.List;

@Service
public class TaskStatusService {
    private final TaskStatusRepo taskStatusRepo;

    public TaskStatusService(TaskStatusRepo taskStatusRepo) {
        this.taskStatusRepo = taskStatusRepo;
    }

    public TaskStatusDto getTaskStatus(Long taskId) {
        TaskStatus taskStatus = taskStatusRepo.findById(taskId).orElse(null);
        if (taskStatus == null)
            return null;
        TaskStatusDto dto = new TaskStatusDto();
        dto.setId(taskStatus.getId());
        dto.setStatus(taskStatus.getStatus());
        dto.setEnabled(taskStatus.getEnabled());
        return dto;
    }

    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusRepo.findAll();
    }

    public void createTaskStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setStatus(taskStatusDto.getStatus());
        taskStatus.setEnabled(taskStatusDto.getEnabled());
        taskStatus.setId(taskStatusDto.getId());
        taskStatusRepo.save(taskStatus);
    }

    public void updateTaskStatus(Long taskId, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(taskId);
        taskStatus.setStatus(taskStatusDto.getStatus());
        taskStatus.setEnabled(taskStatusDto.getEnabled());
        taskStatusRepo.save(taskStatus);
    }

    public void deleteTaskStatus(Long taskId) {
        taskStatusRepo.deleteById(taskId);
    }

}
