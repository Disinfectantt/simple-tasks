package xyz.cringe.simpletasks.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.cringe.simpletasks.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
