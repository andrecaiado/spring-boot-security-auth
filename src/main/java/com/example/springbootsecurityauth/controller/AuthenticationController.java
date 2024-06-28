package com.example.springbootsecurityauth.controller;

import com.example.springbootsecurityauth.dto.LoginDto;
import com.example.springbootsecurityauth.dto.LoginResponse;
import com.example.springbootsecurityauth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginDto loginDto) {
        LoginResponse response = authenticationService.authenticate(loginDto.getUsername(), loginDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
