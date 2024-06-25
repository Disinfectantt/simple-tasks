package xyz.cringe.simpletasks.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.TaskStatusDto;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.repo.TaskStatusRepo;
import xyz.cringe.simpletasks.service.TaskStatusService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskStatusServiceTest {
    @Mock
    private TaskStatusRepo taskStatusRepo;

    @InjectMocks
    private TaskStatusService taskStatusService;

    private TaskStatus taskStatus;
    private TaskStatusDto taskStatusDto;

    @BeforeEach
    void setUp() {
        taskStatus = new TaskStatus();
        taskStatus.setId(1L);
        taskStatus.setStatus("In Progress");
        taskStatus.setEnabled(true);

        taskStatusDto = new TaskStatusDto();
        taskStatusDto.setId(1L);
        taskStatusDto.setStatus("In Progress");
        taskStatusDto.setEnabled(true);
    }

    @Test
    void testGetTaskStatus() {
        when(taskStatusRepo.findById(1L)).thenReturn(Optional.of(taskStatus));

        TaskStatusDto result = taskStatusService.getTaskStatus(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("In Progress");
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void testGetTaskStatusNotFound() {
        when(taskStatusRepo.findById(1L)).thenReturn(Optional.empty());

        TaskStatusDto result = taskStatusService.getTaskStatus(1L);

        assertThat(result).isNull();
    }

    @Test
    void testGetTask() {
        when(taskStatusRepo.findById(1L)).thenReturn(Optional.of(taskStatus));

        TaskStatus result = taskStatusService.getTask(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("In Progress");
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void testGetAllTaskStatus() {
        List<TaskStatus> taskStatuses = Arrays.asList(taskStatus, new TaskStatus());
        when(taskStatusRepo.findAll()).thenReturn(taskStatuses);

        List<TaskStatus> result = taskStatusService.getAllTaskStatus();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getStatus()).isEqualTo("In Progress");
    }

    @Test
    void testCreateTaskStatus() {
        taskStatusService.createTaskStatus(taskStatusDto);

        verify(taskStatusRepo).save(argThat(savedStatus ->
                savedStatus.getId().equals(1L) &&
                        savedStatus.getStatus().equals("In Progress") &&
                        savedStatus.getEnabled()
        ));
    }

    @Test
    void testUpdateTaskStatus() {
        taskStatusService.updateTaskStatus(1L, taskStatusDto);

        verify(taskStatusRepo).save(argThat(savedStatus ->
                savedStatus.getId().equals(1L) &&
                        savedStatus.getStatus().equals("In Progress") &&
                        savedStatus.getEnabled()
        ));
    }

    @Test
    void testDeleteTaskStatus() {
        taskStatusService.deleteTaskStatus(1L);

        verify(taskStatusRepo).deleteById(1L);
    }
}
