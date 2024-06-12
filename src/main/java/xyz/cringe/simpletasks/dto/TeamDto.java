package xyz.cringe.simpletasks.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.cringe.simpletasks.validator.UniqueTeam;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeamDto {
    @Size(min = 1, max = 255)
    @NotNull
    @UniqueTeam
    private String name;
    private Boolean enabled;

}
