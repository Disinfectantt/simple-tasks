package xyz.cringe.simpletasks.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.cringe.simpletasks.model.Task;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.validator.StatusValid;
import xyz.cringe.simpletasks.validator.TeamValid;
import xyz.cringe.simpletasks.validator.WorkersValid;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class TaskDto {
    private Long id;
    @Size(min = 1, max = 255)
    private String name;
    @Size(min = 1, max = 255)
    private String description;
    private Integer priority = 0;
    private Integer difficulty = 0;
    @TeamValid
    private Long teamId;
    @StatusValid
    private Long statusId;
    @WorkersValid
    private Set<Long> workers;

    public TaskDto(Task task) {
        id = task.getId();
        name = task.getName();
        description = task.getDescription();
        priority = task.getPriority();
        difficulty = task.getDifficulty();
        if (task.getTeam() != null)
            teamId = task.getTeam().getId();
        if (task.getStatus() != null)
            statusId = task.getStatus().getId();
        if (task.getWorkers() != null) {
            workers = task.getWorkers().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
        }
    }
}
