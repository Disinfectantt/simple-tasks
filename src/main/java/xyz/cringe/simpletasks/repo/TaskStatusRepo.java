package xyz.cringe.simpletasks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.cringe.simpletasks.model.TaskStatus;

public interface TaskStatusRepo extends JpaRepository<TaskStatus, Long> {
}
