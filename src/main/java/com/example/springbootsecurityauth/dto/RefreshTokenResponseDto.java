package com.example.springbootsecurityauth.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenResponseDto {
    private String accessToken;
    @Builder.Default
    private String type = "Bearer";
    private String refreshToken;
}
