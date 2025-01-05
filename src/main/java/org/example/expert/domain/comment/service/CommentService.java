package org.example.expert.domain.comment.service;

import java.util.ArrayList;
import java.util.List;
import org.example.expert.domain.comment.dto.request.CommentSaveRequestDto;
import org.example.expert.domain.comment.dto.response.CommentResponseDto;
import org.example.expert.domain.comment.dto.response.CommentSaveResponseDto;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponseDto;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    public CommentService(TodoRepository todoRepository, CommentRepository commentRepository) {
        this.todoRepository = todoRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentSaveResponseDto saveComment(
        AuthUserDto authUser,
        long todoId,
        CommentSaveRequestDto requestDto
    ) {
        Todo todo = todoRepository.
            findById(todoId).
            orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = User.fromAuthUser(authUser);

        Comment newComment = new Comment(
            requestDto.getContents(),
            user,
            todo
        );

        Comment savedComment = commentRepository.save(newComment);

        return new CommentSaveResponseDto(
            savedComment.getId(),
            savedComment.getContents(),
            new UserResponseDto(user.getId(), user.getEmail())
        );
    }

    public List<CommentResponseDto> getComments(long todoId) {
        List<Comment> commentList = new ArrayList<>();
        commentList = commentRepository.findAllByTodoId(todoId);

        List<CommentResponseDto> dtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = comment.getUser();
            CommentResponseDto responseDto = new CommentResponseDto(
                comment.getId(),
                comment.getContents(),
                new UserResponseDto(user.getId(), user.getEmail())
            );
            dtoList.add(responseDto);
        }
        return dtoList;
    }
}
