package com.example.springbootsecurityauth.controller;

import com.example.springbootsecurityauth.dto.SignInRequest;
import com.example.springbootsecurityauth.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> login(@RequestBody SignInRequest signInRequest) {
        authenticationService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public String user() {
        return "Hello User!";
    }
}
