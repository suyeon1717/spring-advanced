package org.example.expert.domain.todo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TodoSaveRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String contents;

    public TodoSaveRequestDto() {

    }

    public TodoSaveRequestDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
