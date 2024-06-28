package com.example.springbootsecurityauth.controller;

import com.example.springbootsecurityauth.dto.LoginDto;
import com.example.springbootsecurityauth.dto.LoginResponseDto;
import com.example.springbootsecurityauth.dto.RegisterDto;
import com.example.springbootsecurityauth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> authenticate(@Valid @RequestBody LoginDto loginDto) {
        LoginResponseDto response = authenticationService.authenticate(loginDto.getUsername(), loginDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) throws Exception {
        authenticationService.register(registerDto.getUsername(), registerDto.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body("User registered successfully!");
    }

}
