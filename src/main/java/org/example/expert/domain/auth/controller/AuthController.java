package org.example.expert.domain.auth.controller;

import jakarta.validation.Valid;
import org.example.expert.domain.auth.dto.request.SignInRequestDto;
import org.example.expert.domain.auth.dto.request.SignUpRequestDto;
import org.example.expert.domain.auth.dto.response.SignInResponseDto;
import org.example.expert.domain.auth.dto.response.SignUpResponseDto;
import org.example.expert.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/sign-up")
    public SignUpResponseDto signUp(
        @Valid @RequestBody SignUpRequestDto requestDto
    ) {
        return authService.signUp(requestDto);
    }

    @PostMapping("/auth/sign-in")
    public SignInResponseDto signIn(
        @Valid @RequestBody SignInRequestDto requestDto
    ) {
        return authService.signIn(requestDto);
    }
}
