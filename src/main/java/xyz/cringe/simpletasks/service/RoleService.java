package xyz.cringe.simpletasks.service;

import org.springframework.stereotype.Service;
import xyz.cringe.simpletasks.model.Role;
import xyz.cringe.simpletasks.repo.RoleRepo;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Role findByName(String name) {
        return roleRepo.findByName(name);
    }

    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    public void save(Role role) {
        roleRepo.save(role);
    }
}
