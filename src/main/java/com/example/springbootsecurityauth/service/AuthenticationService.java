package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.dto.LoginResponseDto;
import com.example.springbootsecurityauth.dto.RefreshTokenResponseDto;
import com.example.springbootsecurityauth.entity.CustomUserDetails;
import com.example.springbootsecurityauth.entity.RefreshToken;
import com.example.springbootsecurityauth.entity.User;
import com.example.springbootsecurityauth.enums.RoleEnum;
import com.example.springbootsecurityauth.exception.RefreshTokenException;
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

    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponseDto authenticate(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        userService.updateLastLogin(username);

        CustomUserDetails userDetails = userService.getUserByUsername(username);

        var jwt = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser().getId());

        return LoginResponseDto.builder().accessToken(jwt).refreshToken(refreshToken.getToken()).type("Bearer").build();
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

    public RefreshTokenResponseDto refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        CustomUserDetails userDetails = userService.getUserByUsername(refreshToken.getUser().getUsername());

        String token = jwtService.generateToken(userDetails);

        RefreshTokenResponseDto refreshTokenResponseDto = RefreshTokenResponseDto.builder()
                .accessToken(token)
                .refreshToken(requestRefreshToken)
                .type("Bearer")
                .build();

        return refreshTokenResponseDto;
    }
}
