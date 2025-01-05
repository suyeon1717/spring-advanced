package org.example.expert.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class SignUpResponseDto {

    private final String bearerToken;

    public SignUpResponseDto(String bearerToken) {
        this.bearerToken = bearerToken;
    }
}
