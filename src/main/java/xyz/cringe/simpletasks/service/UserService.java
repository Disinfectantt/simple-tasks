package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.model.User;
import xyz.cringe.simpletasks.repo.UserRepo;

import java.util.List;

@Service
public class UserService {
    private final UserRepo userRepo;

    UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void save(User user) {
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
