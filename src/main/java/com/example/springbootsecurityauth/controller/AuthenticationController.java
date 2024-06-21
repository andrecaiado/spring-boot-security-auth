package com.example.springbootsecurityauth.controller;

import com.example.springbootsecurityauth.dto.SignInRequest;
import com.example.springbootsecurityauth.dto.SignInResponse;
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

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse response = authenticationService.signIn(signInRequest.getUsername(), signInRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String user(Principal principal) {
        return "Hello, " + principal.getName() + "!";
    }
}
