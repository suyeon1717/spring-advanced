package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponseDto;

@Getter
public class ManagerSaveResponse {

    private final Long id;
    private final UserResponseDto user;

    public ManagerSaveResponse(Long id, UserResponseDto user) {
        this.id = id;
        this.user = user;
    }
}
