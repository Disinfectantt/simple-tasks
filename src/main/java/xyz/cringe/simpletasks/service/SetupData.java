package xyz.cringe.simpletasks.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.User;

import java.util.HashSet;
import java.util.Set;

@Component
public class SetupData implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    SetupData(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, TeamService teamService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("ROLE_ADMIN"));
        roles.add(new Role("ROLE_EDITOR"));
        roles.add(new Role("ROLE_WORKER"));
        if (roleService.count() == 0) {
            for (var role : roles) {
                roleService.save(role);
            }
        }

        User defaultUser = userService.findByUsernameUser("admin");
        if (defaultUser == null) {
            defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin"));
            defaultUser.setEnabled(true);
            defaultUser.setRoles(roles);
            userService.save(defaultUser);
        }
    }
}
