package xyz.cringe.simpletasks.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.dto.UserDto;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.repo.TeamRepo;
import xyz.cringe.simpletasks.repo.UserRepo;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final RoleService roleService;
    private final TeamRepo teamRepo;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepo userRepo, RoleService roleService, TeamRepo teamRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleService = roleService;
        this.teamRepo = teamRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto findByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null)
            return null;
        return new UserDto(user);
    }

    public UserDto findById(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null)
            return null;
        return new UserDto(user);
    }

    public User findByUsernameUser(String username) {
        return userRepo.findByUsername(username);
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public void save(UserDto userDto) {
        User user = UserDtoToUser(userDto);
        userRepo.save(user);
    }

    public void update(Long id, UserDto userDto) {
        User user = UserDtoToUser(userDto);
        user.setId(id);
        userRepo.save(user);
    }

    public void delete(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user != null && !user.getUsername().equals("admin")) {
            userRepo.deleteById(id);
        }
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    private User UserDtoToUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        if (user.getTeam() != null) {
            Team team = teamRepo.findById(user.getTeam().getId()).orElse(null);
            if (team != null)
                user.setTeam(team);
        }
        if (userDto.getRoles() != null) {
            user.setRoles(new HashSet<>());
            for (var roleId : userDto.getRoles()) {
                Role role = roleService.findById(roleId);
                user.getRoles().add(role);
            }
        }
        return user;
    }

}
