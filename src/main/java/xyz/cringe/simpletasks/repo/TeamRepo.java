package xyz.cringe.simpletasks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.cringe.simpletasks.model.Team;

public interface TeamRepo extends JpaRepository<Team, Long> {
    public Team findByName(String name);
}
