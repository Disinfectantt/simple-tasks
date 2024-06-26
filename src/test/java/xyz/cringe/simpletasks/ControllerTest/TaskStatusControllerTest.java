package xyz.cringe.simpletasks.ControllerTest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import xyz.cringe.simpletasks.controller.TaskStatusController;
import xyz.cringe.simpletasks.dto.TaskStatusDto;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TaskStatusService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskStatusControllerTest {
    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private TaskStatusController taskStatusController;

    private ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters;

    @BeforeEach
    void setUp() {
        sseEmitters = new ConcurrentHashMap<>();
    }

    @Test
    void testAll() {
        List<TaskStatus> taskStatuses = new ArrayList<>();
        when(taskStatusService.getAllTaskStatus()).thenReturn(taskStatuses);
        when(request.getHeader("HX-Request")).thenReturn(null);

        String result = taskStatusController.all(model, request);

        verify(model).addAttribute("tasksStatuses", taskStatuses);
        verify(model).addAttribute("currentPage", "/tasksStatuses/");
        assertThat(result).isEqualTo("index");
    }

    @Test
    void testAllWithHxRequest() {
        List<TaskStatus> taskStatuses = new ArrayList<>();
        when(taskStatusService.getAllTaskStatus()).thenReturn(taskStatuses);
        when(request.getHeader("HX-Request")).thenReturn("true");

        String result = taskStatusController.all(model, request);

        verify(model).addAttribute("tasksStatuses", taskStatuses);
        assertThat(result).isEqualTo("pages/all_taskStatuses");
    }

    @Test
    void testTeamsForm() {
        TaskStatus taskStatusDto = new TaskStatus();
        String result = taskStatusController.teamsForm(taskStatusDto, model);

        assertThat(taskStatusDto.getEnabled()).isTrue();
        verify(model).addAttribute("taskStatusDto", taskStatusDto);
        assertThat(result).isEqualTo("layouts/forms/add-new-taskStatus");
    }

    @Test
    void testTeamsFormById() {
        Long id = 1L;
        TaskStatusDto taskStatusDto = new TaskStatusDto();
        when(taskStatusService.getTaskStatus(id)).thenReturn(taskStatusDto);

        String result = taskStatusController.teamsFormById(id, model);

        verify(model).addAttribute("taskStatusDto", taskStatusDto);
        assertThat(result).isEqualTo("layouts/forms/add-new-taskStatus");
    }

    @Test
    void testAdd() {
        TaskStatusDto taskStatusDto = new TaskStatusDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = taskStatusController.add(taskStatusDto, bindingResult, model, response);

        verify(taskStatusService).createTaskStatus(taskStatusDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-taskStatus");
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        TaskStatusDto taskStatusDto = new TaskStatusDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = taskStatusController.update(taskStatusDto, bindingResult, model, id, response);

        verify(taskStatusService).updateTaskStatus(id, taskStatusDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-taskStatus");
    }

    @Test
    void testDelete() {
        Long id = 1L;

        ResponseEntity<Void> result = taskStatusController.delete(id);

        verify(taskStatusService).deleteTaskStatus(id);
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testSse() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(sseEmitterService.createSseEmitter(any(), anyString(), any())).thenReturn(mockEmitter);

        SseEmitter result = taskStatusController.sse(userDetails);

        assertThat(result).isEqualTo(mockEmitter);
    }
}
