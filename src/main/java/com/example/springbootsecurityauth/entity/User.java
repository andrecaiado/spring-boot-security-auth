package com.example.springbootsecurityauth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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

    @NotNull()
    @Column(name = "password")
    private String password;

    @Column(name = "last_login")
    private Instant lastLogin;

    @Column(name = "roles")
    private String roles;
}
