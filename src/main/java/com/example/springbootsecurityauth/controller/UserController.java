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

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getUserProfile(Principal principal) {
        UserProfileDto userProfile = userService.getUserProfile(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(userProfile);
    }
}
