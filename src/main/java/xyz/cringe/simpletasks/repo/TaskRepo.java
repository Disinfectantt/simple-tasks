package xyz.cringe.simpletasks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.cringe.simpletasks.model.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t JOIN t.status WHERE t.id = :id")
    Task findByIdWithStatus(@Param("id") Long id);
}
