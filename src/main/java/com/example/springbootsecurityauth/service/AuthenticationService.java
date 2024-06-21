package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.dto.SignInResponse;
import com.example.springbootsecurityauth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JpaUserDetailsService jpaUserDetailsService;

    private final JwtService jwtService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepository, JpaUserDetailsService jpaUserDetailsService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jpaUserDetailsService = jpaUserDetailsService;
        this.jwtService = jwtService;
    }

    public SignInResponse signIn(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        UserDetails userDetails = jpaUserDetailsService.loadUserByUsername(username);
        var jwt = jwtService.generateToken(userDetails);
        return SignInResponse.builder().token(jwt).build();
    }
}
