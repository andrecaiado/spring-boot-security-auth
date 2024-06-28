package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.dto.LoginResponse;
import com.example.springbootsecurityauth.entity.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtService jwtService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponse authenticate(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        CustomUserDetails userDetails = userService.getUserByUsername(username);
        var jwt = jwtService.generateToken(userDetails);
        return LoginResponse.builder().token(jwt).build();
    }
}
