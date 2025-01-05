package org.example.expert.domain.todo.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.response.UserResponseDto;

@Getter
public class TodoSaveResponseDto {

    private final Long id;
    private final String title;
    private final String contents;
    private final String weather;
    private final UserResponseDto user;

    public TodoSaveResponseDto(
        Long id, String title, String contents,
        String weather, UserResponseDto user
    ) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.weather = weather;
        this.user = user;
    }
}
