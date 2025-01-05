package org.example.expert.domain.todo.controller;

import jakarta.validation.Valid;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.todo.dto.request.TodoSaveRequestDto;
import org.example.expert.domain.todo.dto.response.TodoResponseDto;
import org.example.expert.domain.todo.dto.response.TodoSaveResponseDto;
import org.example.expert.domain.todo.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/todos")
    public ResponseEntity<TodoSaveResponseDto> saveTodo(
        @Auth AuthUserDto authUser,
        @Valid @RequestBody TodoSaveRequestDto requestDto
    ) {
        return new ResponseEntity<>(todoService.saveTodo(authUser, requestDto), HttpStatus.OK);
    }

    @GetMapping("/todos")
    public ResponseEntity<Page<TodoResponseDto>> getTodos(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(todoService.getTodos(page, size), HttpStatus.OK);
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<TodoResponseDto> getTodo(@PathVariable long todoId) {
        return new ResponseEntity<>(todoService.getTodo(todoId), HttpStatus.OK);
    }
}
