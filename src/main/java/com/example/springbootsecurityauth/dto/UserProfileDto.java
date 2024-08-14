package com.example.springbootsecurityauth.dto;

import com.example.springbootsecurityauth.enums.RoleEnum;

import java.time.Instant;
import java.util.List;

public record UserProfileDto(String username, Instant lastLogin, List<String> roles) {
}
