package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.entity.Role;
import com.example.springbootsecurityauth.enums.RoleEnum;
import com.example.springbootsecurityauth.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    Role findByName(RoleEnum name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role is not found: " + name));
    }
}
