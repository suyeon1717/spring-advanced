package org.example.expert.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UserRoleChangeRequestDto {

    private String role;

    public UserRoleChangeRequestDto() {

    }

    public UserRoleChangeRequestDto(String role) {
        this.role = role;
    }
}
