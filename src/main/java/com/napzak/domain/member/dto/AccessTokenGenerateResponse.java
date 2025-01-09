package com.napzak.domain.member.dto;

public record AccessTokenGenerateResponse(
        String accessToken
) {
    public static AccessTokenGenerateResponse from(
            final String accessToken
    ) {
        return new AccessTokenGenerateResponse(accessToken);
    }
}