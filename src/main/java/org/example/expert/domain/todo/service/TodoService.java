package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.common.service.CommonService;
import org.example.expert.domain.todo.dto.request.TodoSaveRequestDto;
import org.example.expert.domain.todo.dto.response.TodoResponseDto;
import org.example.expert.domain.todo.dto.response.TodoSaveResponseDto;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponseDto;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;
    private final CommonService commonService;

    public TodoService(TodoRepository todoRepository, WeatherClient weatherClient,
        CommonService commonService) {
        this.todoRepository = todoRepository;
        this.weatherClient = weatherClient;
        this.commonService = commonService;
    }


    @Transactional
    public TodoSaveResponseDto saveTodo(AuthUserDto authUser, TodoSaveRequestDto requestDto) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
            requestDto.getTitle(),
            requestDto.getContents(),
            weather,
            user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponseDto(
            savedTodo.getId(),
            savedTodo.getTitle(),
            savedTodo.getContents(),
            weather,
            new UserResponseDto(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponseDto> getTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Todo> todos = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        return todos.map(todo -> new TodoResponseDto(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponseDto(todo.getUser().getId(), todo.getUser().getEmail()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
        ));
    }

    public TodoResponseDto getTodo(long todoId) {
        Todo todo = commonService.findEntityById(
            todoRepository,
            todoId,
            "Todo not found"
        );

        User user = todo.getUser();

        return new TodoResponseDto(
            todo.getId(),
            todo.getTitle(),
            todo.getContents(),
            todo.getWeather(),
            new UserResponseDto(user.getId(), user.getEmail()),
            todo.getCreatedAt(),
            todo.getModifiedAt()
        );
    }
}
