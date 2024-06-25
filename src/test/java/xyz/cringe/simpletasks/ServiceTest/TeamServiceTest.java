package xyz.cringe.simpletasks.ServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.repo.TeamRepo;
import xyz.cringe.simpletasks.service.TeamService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamRepo teamRepo;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private TeamDto teamDto;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Test Team");
        team.setEnabled(true);

        teamDto = new TeamDto();
        teamDto.setId(1L);
        teamDto.setName("Test Team");
        teamDto.setEnabled(true);
    }

    @Test
    void testGetAllTeams() {
        List<Team> teams = Arrays.asList(team, new Team());
        when(teamRepo.findAll()).thenReturn(teams);

        List<Team> result = teamService.getAllTeams();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getName()).isEqualTo("Test Team");
    }

    @Test
    void testGetTeamById() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));

        TeamDto result = teamService.getTeamById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Team");
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void testGetTeamByIdNotFound() {
        when(teamRepo.findById(1L)).thenReturn(Optional.empty());

        TeamDto result = teamService.getTeamById(1L);

        assertThat(result).isNull();
    }

    @Test
    void testGetTeam() {
        when(teamRepo.findById(1L)).thenReturn(Optional.of(team));

        Team result = teamService.getTeam(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Team");
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void testCreateTeam() {
        teamService.createTeam(teamDto);

        verify(teamRepo).save(argThat(savedTeam ->
                savedTeam.getName().equals("Test Team") &&
                        savedTeam.getEnabled()
        ));
    }

    @Test
    void testGetTeamByName() {
        when(teamRepo.findByName("Test Team")).thenReturn(team);

        TeamDto result = teamService.getTeamByName("Test Team");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Team");
        assertThat(result.getEnabled()).isTrue();
    }

    @Test
    void testGetTeamByNameNotFound() {
        when(teamRepo.findByName("Non-existent Team")).thenReturn(null);

        TeamDto result = teamService.getTeamByName("Non-existent Team");

        assertThat(result).isNull();
    }

    @Test
    void testDeleteTeamById() {
        teamService.deleteTeamById(1L);

        verify(teamRepo).deleteById(1L);
    }

    @Test
    void testUpdateTeam() {
        teamService.updateTeam(teamDto, 1L);

        verify(teamRepo).save(argThat(savedTeam ->
                savedTeam.getId().equals(1L) &&
                        savedTeam.getName().equals("Test Team") &&
                        savedTeam.getEnabled()
        ));
    }
}
