package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.dto.TeamDto;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.repo.TeamRepo;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepo teamRepo;

    TeamService(TeamRepo teamRepo) {
        this.teamRepo = teamRepo;
    }

    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    public TeamDto getTeamById(Long id) {
        Team team = teamRepo.findById(id).orElse(null);
        if (team != null) {
            return setTeamDto(team);
        }
        return null;
    }

    public void createTeam(TeamDto teamDto) {
        Team team = new Team();
        team.setName(teamDto.getName());
        team.setEnabled(teamDto.getEnabled());
        teamRepo.save(team);
    }

    public TeamDto getTeamByName(String name) {
        Team team = teamRepo.findByName(name);
        if (team != null) {
            return setTeamDto(team);
        }
        return null;
    }

    public void deleteTeamById(Long id) {
        teamRepo.deleteById(id);
    }

    public void updateTeam(TeamDto teamDto, Long id) {
        Team team = new Team();
        team.setName(teamDto.getName());
        team.setEnabled(teamDto.getEnabled());
        team.setId(id);
        teamRepo.save(team);
    }

    private TeamDto setTeamDto(Team team) {
        TeamDto teamDto = new TeamDto();
        teamDto.setName(team.getName());
        teamDto.setEnabled(team.getEnabled());
        teamDto.setId(team.getId());
        return teamDto;
    }

}
