package xyz.cringe.simpletasks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.cringe.simpletasks.model.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {
}
