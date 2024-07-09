package com.example.springbootsecurityauth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenResponseDto {
    private String accessToken;
    private String type;
    private String refreshToken;
}
