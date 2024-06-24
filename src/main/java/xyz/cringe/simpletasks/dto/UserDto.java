package xyz.cringe.simpletasks.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.User;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    @Size(min = 1, max = 255)
    @NotNull
    private String username;
    @Size(min = 1, max = 255)
    @NotNull
    private String password;
    private String email;
    private Boolean enabled = false;
    private Set<RoleDto> roles;
    private TeamDto team;

    public UserDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.enabled = user.getEnabled();
        for (Role role : user.getRoles()) {
            roles.add(new RoleDto(role.getName()));
        }
        this.team.setName(user.getTeam().getName());
        this.team.setId(user.getTeam().getId());
        this.team.setEnabled(user.getTeam().getEnabled());
    }
}
