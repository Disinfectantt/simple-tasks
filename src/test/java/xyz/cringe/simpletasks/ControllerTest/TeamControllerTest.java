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
import xyz.cringe.simpletasks.controller.TeamController;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.service.SseEmitterService;
import xyz.cringe.simpletasks.service.TeamService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamControllerTest {
    @Mock
    private TeamService teamService;

    @Mock
    private SseEmitterService sseEmitterService;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private TeamController teamController;

    private ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters;

    @BeforeEach
    void setUp() {
        sseEmitters = new ConcurrentHashMap<>();
    }

    @Test
    void testAll_NonHxRequest() {
        List<Team> teams = new ArrayList<>();
        when(teamService.getAllTeams()).thenReturn(teams);
        when(request.getHeader("HX-Request")).thenReturn(null);

        String result = teamController.all(model, request);

        verify(model).addAttribute("teams", teams);
        verify(model).addAttribute("currentPage", "/teams/");
        assertThat(result).isEqualTo("index");
    }

    @Test
    void testAll_HxRequest() {
        List<Team> teams = new ArrayList<>();
        when(teamService.getAllTeams()).thenReturn(teams);
        when(request.getHeader("HX-Request")).thenReturn("true");

        String result = teamController.all(model, request);

        verify(model).addAttribute("teams", teams);
        assertThat(result).isEqualTo("pages/all_teams");
    }

    @Test
    void testTeamsForm() {
        TeamDto teamDto = new TeamDto();

        String result = teamController.teamsForm(teamDto);

        assertThat(teamDto.getEnabled()).isTrue();
        assertThat(result).isEqualTo("layouts/forms/add-new-team");
    }

    @Test
    void testTeamsFormById() {
        Long id = 1L;
        TeamDto teamDto = new TeamDto();
        when(teamService.getTeamById(id)).thenReturn(teamDto);

        String result = teamController.teamsFormById(id, model);

        verify(teamService).getTeamById(id);
        verify(model).addAttribute("teamDto", teamDto);
        assertThat(result).isEqualTo("layouts/forms/add-new-team");
    }

    @Test
    void testAdd_Success() {
        TeamDto teamDto = new TeamDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = teamController.add(teamDto, bindingResult, model, response);

        verify(teamService).createTeam(teamDto);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-team");
    }

    @Test
    void testAdd_Error() {
        TeamDto teamDto = new TeamDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = teamController.add(teamDto, bindingResult, model, response);

        verify(model).addAttribute("teamDto", teamDto);
        verifyNoInteractions(teamService, sseEmitterService);
        assertThat(result).isEqualTo("layouts/forms/add-new-team");
    }

    @Test
    void testUpdate_Success() {
        Long id = 1L;
        TeamDto teamDto = new TeamDto();
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = teamController.update(teamDto, bindingResult, model, id, response);

        verify(teamService).updateTeam(teamDto, id);
        verify(response).addHeader("Hx-Trigger", "closeModal");
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result).isEqualTo("layouts/forms/add-new-team");
    }

    @Test
    void testUpdate_Error() {
        Long id = 1L;
        TeamDto teamDto = new TeamDto();
        when(bindingResult.hasErrors()).thenReturn(true);

        String result = teamController.update(teamDto, bindingResult, model, id, response);

        verify(model).addAttribute("teamDto", teamDto);
        verifyNoInteractions(teamService, sseEmitterService);
        assertThat(result).isEqualTo("layouts/forms/add-new-team");
    }

    @Test
    void testDelete() {
        Long id = 1L;

        ResponseEntity<Void> result = teamController.delete(id);

        verify(teamService).deleteTeamById(id);
        verify(sseEmitterService).sendEvent(any(), eq(sseEmitters), any());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testSse() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        SseEmitter mockEmitter = mock(SseEmitter.class);
        when(sseEmitterService.createSseEmitter(any(), anyString(), any())).thenReturn(mockEmitter);

        SseEmitter result = teamController.sse(userDetails);

        assertThat(result).isEqualTo(mockEmitter);
    }
}
