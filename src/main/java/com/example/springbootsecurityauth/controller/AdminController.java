package com.example.springbootsecurityauth.controller;

import com.example.springbootsecurityauth.dto.UserProfileDto;
import com.example.springbootsecurityauth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileDto>> userAllUsersProfiles() {
        List<UserProfileDto> usersProfiles = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(usersProfiles);
    }
}
