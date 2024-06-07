package xyz.cringe.simpletasks.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.User;

import java.util.Collections;

@Component
public class SetupData implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final TeamService teamService;

    SetupData(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder, TeamService teamService) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.teamService = teamService;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleService.findByName("admin");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("admin");
            roleService.save(adminRole);
        }

        User defaultUser = userService.findByUsername("admin");
        if (defaultUser == null) {
            defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setPassword(passwordEncoder.encode("admin"));
            defaultUser.setEnabled(true);
            defaultUser.setRoles(Collections.singleton(adminRole));
            userService.save(defaultUser);
        }
    }
}
