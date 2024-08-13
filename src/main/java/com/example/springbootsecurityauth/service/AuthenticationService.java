package com.example.springbootsecurityauth.service;

import com.example.springbootsecurityauth.dto.LoginResponseDto;
import com.example.springbootsecurityauth.dto.RefreshTokenResponseDto;
import com.example.springbootsecurityauth.entity.AppUser;
import com.example.springbootsecurityauth.entity.RefreshToken;
import com.example.springbootsecurityauth.entity.Role;
import com.example.springbootsecurityauth.enums.RoleEnum;
import com.example.springbootsecurityauth.exception.RefreshTokenException;
import com.example.springbootsecurityauth.exception.UsernameAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    private final CustomUserDetailsService userDetailsService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    private final RoleService roleService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, CustomUserDetailsService userDetailsService, JwtService jwtService, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.roleService = roleService;
    }

    public LoginResponseDto authenticate(String username, String password) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationManager.authenticate(authentication);

        userService.updateLastLogin(username);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        var jwt = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());

        return LoginResponseDto.builder().accessToken(jwt).refreshToken(refreshToken.getToken()).build();
    }

    public void register(String username, String password, Set<String> roles) {
        if (userService.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        List<Role> userRoles = new ArrayList<>();

        roles.forEach(role -> {
            if (EnumUtils.findEnumInsensitiveCase(RoleEnum.class, role) == null) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
            Role roleEntity = roleService.findByName(RoleEnum.valueOf(role));
            userRoles.add(roleEntity);
        });

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(userRoles);

        userService.createUser(user);
    }

    public RefreshTokenResponseDto refreshToken(String requestRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken, "Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUser().getUsername());

        String token = jwtService.generateToken(userDetails);

        RefreshTokenResponseDto refreshTokenResponseDto = RefreshTokenResponseDto.builder()
                .accessToken(token)
                .refreshToken(requestRefreshToken)
                .build();

        return refreshTokenResponseDto;
    }
}
