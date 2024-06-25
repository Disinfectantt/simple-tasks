package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.dto.TaskDto;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.repo.TaskRepo;

import java.util.HashSet;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepo taskRepo;
    private final TeamService teamService;
    private final TaskStatusService taskStatusService;
    private final UserService userService;

    public TaskService(TaskRepo taskRepo, TeamService teamService, TaskStatusService taskStatusService, UserService userService) {
        this.taskRepo = taskRepo;
        this.teamService = teamService;
        this.taskStatusService = taskStatusService;
        this.userService = userService;
    }

    public void create(TaskDto taskDto) {
        Task task = taskDtoToTask(taskDto);
        taskRepo.save(task);
    }

    public TaskDto getTask(Long id) {
        Task task = taskRepo.findByIdWithStatus(id);
        if (task == null)
            return null;
        return new TaskDto(task);
    }

    public List<Task> findAll() {
        return taskRepo.findAll();
    }

    public void update(Long id, TaskDto taskDto) {
        Task task = taskDtoToTask(taskDto);
        task.setId(id);
        taskRepo.save(task);
    }

    public void updateCurrentWorker(Long id, String username) {
        Task task = taskRepo.findByIdWithStatus(id);
        if (task != null) {
            if (task.getWorkers() == null)
                task.setWorkers(new HashSet<>());
            task.getWorkers().add(userService.findByUsernameUser(username));
            taskRepo.save(task);
        }
    }

    public void delete(Long id) {
        taskRepo.deleteById(id);
    }

    private Task taskDtoToTask(TaskDto taskDto) {
        Task task = new Task();
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setDifficulty(taskDto.getDifficulty());
        task.setPriority(taskDto.getPriority());
        task.setStatus(taskStatusService.getTask(taskDto.getStatusId()));
        task.setTeam(teamService.getTeam(taskDto.getTeamId()));
        return task;
    }
}
