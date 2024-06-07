package xyz.cringe.simpletasks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.cringe.simpletasks.model.User;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
