package xyz.cringe.simpletasks.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.TaskDto;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.repo.TaskRepo;
import xyz.cringe.simpletasks.service.TaskService;
import xyz.cringe.simpletasks.service.TaskStatusService;
import xyz.cringe.simpletasks.service.TeamService;
import xyz.cringe.simpletasks.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepo taskRepo;

    @Mock
    private TeamService teamService;

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDto taskDto;

    @BeforeEach
    public void setUp() {
        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setDescription("Test Description");
        task.setDifficulty(3);
        task.setPriority(2);

        taskDto = new TaskDto();
        taskDto.setName("New Task");
        taskDto.setDescription("New Description");
        taskDto.setDifficulty(2);
        taskDto.setPriority(1);
        taskDto.setStatusId(1L);
        taskDto.setTeamId(1L);
    }

    @Test
    public void testCreate() {
        taskService.create(taskDto);
        verify(taskRepo).save(any(Task.class));
    }

    @Test
    public void testGetTask() {
        when(taskRepo.findByIdWithStatus(1L)).thenReturn(task);
        TaskDto found = taskService.getTask(1L);
        assertThat(found.getName()).isEqualTo("Test Task");
    }

    @Test
    public void testFindAll() {
        when(taskRepo.findAll()).thenReturn(Collections.singletonList(task));
        List<Task> tasks = taskService.findAll();
        assertThat(tasks).hasSize(1);
        assertThat(tasks.getFirst().getName()).isEqualTo("Test Task");
    }

    @Test
    public void testUpdate() {
        taskService.update(1L, taskDto);
        verify(taskRepo).save(any(Task.class));
    }

    @Test
    public void testUpdateCurrentWorker() {
        when(taskRepo.findByIdWithStatus(1L)).thenReturn(task);
        when(userService.findByUsernameUser("testUser")).thenReturn(new User());
        taskService.updateCurrentWorker(1L, "testUser");
        verify(taskRepo).save(any(Task.class));
    }

    @Test
    public void testDelete() {
        taskService.delete(1L);
        verify(taskRepo).deleteById(1L);
    }

    @Test
    public void testTaskDtoToTask() {
        when(taskStatusService.getTask(1L)).thenReturn(new TaskStatus());
        when(teamService.getTeam(1L)).thenReturn(new Team());

        taskService.create(taskDto);

        verify(taskRepo).save(argThat(savedTask ->
                savedTask.getName().equals("New Task") &&
                        savedTask.getDescription().equals("New Description") &&
                        savedTask.getDifficulty() == 2 &&
                        savedTask.getPriority() == 1
        ));
    }
}
