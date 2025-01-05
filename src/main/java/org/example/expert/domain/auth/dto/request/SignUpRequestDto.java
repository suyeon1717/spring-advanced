package org.example.expert.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String userRole;

    public SignUpRequestDto() {
    }

    public SignUpRequestDto(String email, String password, String userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
