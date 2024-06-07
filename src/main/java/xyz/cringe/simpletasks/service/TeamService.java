package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
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

    public Team getTeamById(Long id) {
        return teamRepo.findById(id).orElse(null);
    }

    public void createTeam(Team team) {
        teamRepo.save(team);
    }
}
