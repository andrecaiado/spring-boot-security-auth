package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.dto.UserProfileDto;
import com.example.springbootsecurityauth.entity.User;
import com.example.springbootsecurityauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public UserProfileDto getUserProfile(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserProfileDto(user.getUsername(), user.getLastLogin(), user.getRoles()))
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
    }

    public List<UserProfileDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserProfileDto(user.getUsername(), user.getLastLogin(), user.getRoles()))
                .collect(Collectors.toList());
    }

    public void updateLastLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLogin(Instant.now());
                    userRepository.save(user);
                });
    }
}
