package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.dto.UserDto;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.model.Team;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.repo.UserRepo;

import java.util.HashSet;
import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;

    UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserDto findByUsername(String username) {
        User user = userRepo.findByUsername(username);
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
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        if (user.getTeam() != null) {
            Team team = new Team();
            team.setName(userDto.getTeam().getName());
            team.setId(userDto.getTeam().getId());
            team.setEnabled(userDto.getTeam().getEnabled());
            user.setTeam(team);
        }
        if (userDto.getRoles() != null) {
            user.setRoles(new HashSet<>());
            Role role = new Role();
            for (var roleDto : userDto.getRoles()) {
                role.setName(roleDto.getName());
                role.setId(roleDto.getId());
                role.setEnabled(roleDto.getEnabled());
                user.getRoles().add(role);
            }
        }
        userRepo.save(user);
    }

    public void delete(User user) {
        if (!user.getUsername().equals("admin")) {
            userRepo.delete(user);
        }
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

}
