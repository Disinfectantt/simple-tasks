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
import xyz.cringe.simpletasks.controller.UserController;
import xyz.cringe.simpletasks.dto.UserDto;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.service.RoleService;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TeamService;
import xyz.cringe.simpletasks.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private TeamService teamService;

    @Mock
    private RoleService roleService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters;

    @BeforeEach
    void setUp() {
        sseEmitters = new ConcurrentHashMap<>();
    }

    @Test
    void testAll_NonHxRequest() {
        List<User> users = new ArrayList<>();
        when(userService.findAll()).thenReturn(users);
        when(request.getHeader("HX-Request")).thenReturn(null);

        String result = userController.all(model, request);

        verify(model).addAttribute("users", users);
        verify(model).addAttribute("currentPage", "/tasks/");
        assertThat(result).isEqualTo("index");
    }

    @Test
    void testAll_HxRequest() {
        List<User> users = new ArrayList<>();
        when(userService.findAll()).thenReturn(users);
        when(request.getHeader("HX-Request")).thenReturn("true");

        String result = userController.all(model, request);

        verify(model).addAttribute("users", users);
        assertThat(result).isEqualTo("pages/all_users");
    }

    @Test
    void testTeamsForm() {
        UserDto userDto = new UserDto();
        List<Team> teams = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        when(teamService.getAllTeams()).thenReturn(teams);
        when(roleService.findAll()).thenReturn(roles);

        String result = userController.teamsForm(userDto, model);

        verify(model).addAttribute("userDto", userDto);
        verify(model).addAttribute("teams", teams);
        verify(model).addAttribute("roles", roles);
        assertThat(result).isEqualTo("layouts/forms/add-new-user");
    }

    @Test
    void testTeamsFormById() {
        Long id = 1L;
        UserDto userDto = new UserDto();
        List<Team> teams = new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        when(userService.findById(id)).thenReturn(userDto);
        when(teamService.getAllTeams()).thenReturn(teams);
        when(roleService.findAll()).thenReturn(roles);

        String result = userController.teamsFormById(id, model);

        verify(model).addAttribute("userDto", userDto);
        verify(model).addAttribute("teams", teams);
        verify(model).addAttribute("roles", roles);
        assertThat(result).isEqualTo("layouts/forms/add-new-user");
    }

    @Test
    void testAdd_Success() {
        UserDto userDto = new UserDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = userController.add(userDto, bindingResult, model, response);

        verify(userService).save(userDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-user");
    }

    @Test
    void testAdd_Error() {
        UserDto userDto = new UserDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = userController.add(userDto, bindingResult, model, response);

        verify(model).addAttribute("userDto", userDto);
        verifyNoInteractions(userService, sseEmitterService);
        assertThat(result).isEqualTo("layouts/forms/add-new-user");
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
        UserDto userDto = new UserDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = userController.update(userDto, bindingResult, model, id, response);

        verify(userService).update(id, userDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-user");
    }

    @Test
    void testUpdate_Error() {
        Long id = 1L;
        UserDto userDto = new UserDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = userController.update(userDto, bindingResult, model, id, response);

        verify(model).addAttribute("userDto", userDto);
        verifyNoInteractions(userService, sseEmitterService);
        assertThat(result).isEqualTo("layouts/forms/add-new-user");
    }

    @Test
    void testDelete() {
        Long id = 1L;

        ResponseEntity<Void> result = userController.delete(id);

        verify(userService).delete(id);
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testSse() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(sseEmitterService.createSseEmitter(any(), anyString(), any())).thenReturn(mockEmitter);

        SseEmitter result = userController.sse(userDetails);

        assertThat(result).isEqualTo(mockEmitter);
    }
}
