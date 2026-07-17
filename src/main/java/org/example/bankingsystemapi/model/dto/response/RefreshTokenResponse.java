package org.example.bankingsystemapi.model.dto.response;

public record RefreshTokenResponse(
        String accessToken ,
        String refreshToken
) {
}
