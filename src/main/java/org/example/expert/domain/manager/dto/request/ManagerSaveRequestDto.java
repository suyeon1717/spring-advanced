package org.example.expert.domain.manager.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ManagerSaveRequestDto {

    @NotNull
    private Long managerUserId; // 일정 작상자가 배치하는 유저 id

    public ManagerSaveRequestDto() {

    }

    public ManagerSaveRequestDto(Long managerUserId) {
        this.managerUserId = managerUserId;
    }
}
