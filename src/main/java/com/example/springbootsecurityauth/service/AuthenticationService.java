package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.dto.LoginResponseDto;
import com.example.springbootsecurityauth.entity.CustomUserDetails;
import com.example.springbootsecurityauth.entity.User;
import com.example.springbootsecurityauth.enums.RoleEnum;
import com.example.springbootsecurityauth.exception.UsernameAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponseDto authenticate(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        CustomUserDetails userDetails = userService.getUserByUsername(username);
        var jwt = jwtService.generateToken(userDetails);
        return LoginResponseDto.builder().token(jwt).build();
    }

    public void register(String username, String password, Set<String> roles) {
        if (userService.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        Set<RoleEnum> userRoles = new HashSet<>();

        roles.forEach(role -> {
            if (EnumUtils.findEnumInsensitiveCase(RoleEnum.class, role) == null) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
            userRoles.add(RoleEnum.valueOf(role));
        });

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(userRoles);

        userService.createUser(user);
    }
}
