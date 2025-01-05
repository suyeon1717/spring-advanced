package org.example.expert.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignInRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    public SignInRequestDto() {
    }

    public SignInRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
