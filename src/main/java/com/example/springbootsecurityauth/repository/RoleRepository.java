package com.example.springbootsecurityauth.repository;

import com.example.springbootsecurityauth.entity.Role;
import com.example.springbootsecurityauth.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleEnum name);
}
