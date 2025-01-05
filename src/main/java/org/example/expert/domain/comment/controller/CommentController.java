package org.example.expert.domain.comment.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.expert.domain.comment.dto.request.CommentSaveRequestDto;
import org.example.expert.domain.comment.dto.response.CommentResponseDto;
import org.example.expert.domain.comment.dto.response.CommentSaveResponseDto;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/todos/{todoId}/comments")
    public ResponseEntity<CommentSaveResponseDto> saveComment(
        @Auth AuthUserDto authUser,
        @PathVariable long todoId,
        @Valid @RequestBody CommentSaveRequestDto requestDto
    ) {
        return new ResponseEntity<>(
            commentService.saveComment(
                authUser,
                todoId,
                requestDto
            ),
            HttpStatus.OK
        );
    }

    @GetMapping("/todos/{todoId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable long todoId) {
        return new ResponseEntity<>(commentService.getComments(todoId), HttpStatus.OK);
    }
}
