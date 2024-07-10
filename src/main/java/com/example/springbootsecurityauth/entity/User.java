package com.example.springbootsecurityauth.entity;

import com.example.springbootsecurityauth.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull()
    @Column(unique = true, name = "username")
    private String username;

    @JsonIgnore()
    @NotNull()
    @Column(name = "password")
    private String password;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private Set<RoleEnum> roles;
}
