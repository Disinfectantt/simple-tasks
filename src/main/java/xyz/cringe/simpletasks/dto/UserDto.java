package xyz.cringe.simpletasks.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.User;

import java.util.HashSet;
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
    private Set<Long> roles;
    private Long teamId;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.enabled = user.getEnabled();
        if (user.getRoles() != null) {
            roles = new HashSet<>();
            for (Role role : user.getRoles()) {
                roles.add(role.getId());
            }
        }
        if (user.getTeam() != null)
            this.teamId = user.getTeam().getId();
    }
}
