package xyz.cringe.simpletasks.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TaskStatusDto {
    private Long id;
    @Size(min = 1, max = 255)
    private String status;
    private Boolean enabled = true;
}
