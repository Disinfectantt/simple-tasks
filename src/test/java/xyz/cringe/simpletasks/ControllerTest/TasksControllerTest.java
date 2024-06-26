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
import xyz.cringe.simpletasks.controller.TasksController;
import xyz.cringe.simpletasks.dto.TaskDto;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.model.TaskStatus;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TaskService;
import xyz.cringe.simpletasks.service.TaskStatusService;
import xyz.cringe.simpletasks.service.TeamService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TasksControllerTest {
    @Mock
    private TaskService taskService;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private TeamService teamService;

    @Mock
    private TaskStatusService taskStatusService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private TasksController tasksController;

    private ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters;

    @BeforeEach
    void setUp() {
        sseEmitters = new ConcurrentHashMap<>();
    }

    @Test
    void testAll_NonHxRequest() {
        List<Task> tasks = new ArrayList<>();
        when(taskService.findAll()).thenReturn(tasks);
        when(request.getHeader("HX-Request")).thenReturn(null);

        String result = tasksController.all(model, request);

        verify(model).addAttribute("tasks", tasks);
        verify(model).addAttribute("currentPage", "/tasks/");
        assertThat(result).isEqualTo("index");
    }

    @Test
    void testAll_HxRequest() {
        List<Task> tasks = new ArrayList<>();
        when(taskService.findAll()).thenReturn(tasks);
        when(request.getHeader("HX-Request")).thenReturn("true");

        String result = tasksController.all(model, request);

        verify(model).addAttribute("tasks", tasks);
        assertThat(result).isEqualTo("pages/all_tasks");
    }

    @Test
    void testTeamsForm() {
        TaskDto taskDto = new TaskDto();
        List<Team> teams = new ArrayList<>();
        List<TaskStatus> statuses = new ArrayList<>();
        when(teamService.getAllTeams()).thenReturn(teams);
        when(taskStatusService.getAllTaskStatus()).thenReturn(statuses);

        String result = tasksController.teamsForm(taskDto, model);

        assertThat(taskDto.getDifficulty()).isEqualTo(0);
        assertThat(taskDto.getPriority()).isEqualTo(0);
        verify(model).addAttribute("taskDto", taskDto);
        verify(model).addAttribute("teams", teams);
        verify(model).addAttribute("statuses", statuses);
        assertThat(result).isEqualTo("layouts/forms/add-new-task");
    }

    @Test
    void testTeamsFormById() {
        Long id = 1L;
        TaskDto taskDto = new TaskDto();
        List<Team> teams = new ArrayList<>();
        List<TaskStatus> statuses = new ArrayList<>();
        when(taskService.getTask(id)).thenReturn(taskDto);
        when(teamService.getAllTeams()).thenReturn(teams);
        when(taskStatusService.getAllTaskStatus()).thenReturn(statuses);

        String result = tasksController.teamsFormById(id, model);

        verify(model).addAttribute("taskDto", taskDto);
        verify(model).addAttribute("teams", teams);
        verify(model).addAttribute("statuses", statuses);
        assertThat(result).isEqualTo("layouts/forms/add-new-task");
    }

    @Test
    void testAdd_Success() {
        TaskDto taskDto = new TaskDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = tasksController.add(taskDto, bindingResult, model, response);

        verify(taskService).create(taskDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-task");
    }

    @Test
    void testAdd_Error() {
        TaskDto taskDto = new TaskDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = tasksController.add(taskDto, bindingResult, model, response);

        verify(model).addAttribute("taskDto", taskDto);
        verifyNoInteractions(taskService, sseEmitterService);
        assertThat(result).isEqualTo("layouts/forms/add-new-task");
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
        TaskDto taskDto = new TaskDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = tasksController.update(taskDto, bindingResult, model, id, response);

        verify(taskService).update(id, taskDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-task");
    }

    @Test
    void testUpdate_Error() {
        Long id = 1L;
        TaskDto taskDto = new TaskDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = tasksController.update(taskDto, bindingResult, model, id, response);

        verify(model).addAttribute("taskDto", taskDto);
        verifyNoInteractions(taskService, sseEmitterService);
        assertThat(result).isEqualTo("layouts/forms/add-new-task");
    }

    @Test
    void testDelete() {
        Long id = 1L;

        ResponseEntity<Void> result = tasksController.delete(id);

        verify(taskService).delete(id);
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testSse() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(sseEmitterService.createSseEmitter(any(), anyString(), any())).thenReturn(mockEmitter);

        SseEmitter result = tasksController.sse(userDetails);

        assertThat(result).isEqualTo(mockEmitter);
    }

    @Test
    void testTake() {
        Long id = 1L;
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("testUser");

        ResponseEntity<Void> result = tasksController.take(id, principal);

        verify(taskService).updateCurrentWorker(id, "testUser");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
