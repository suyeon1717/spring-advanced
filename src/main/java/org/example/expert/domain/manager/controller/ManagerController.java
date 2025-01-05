package org.example.expert.domain.manager.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import java.util.List;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequestDto;
import org.example.expert.domain.manager.dto.response.ManagerResponseDto;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponseDto;
import org.example.expert.domain.manager.service.ManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManagerController {

    private final ManagerService managerService;
    private final JwtUtil jwtUtil;

    public ManagerController(ManagerService managerService, JwtUtil jwtUtil) {
        this.managerService = managerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/todos/{todoId}/managers")
    public ResponseEntity<ManagerSaveResponseDto> saveManager(
        @Auth AuthUserDto authUser,
        @PathVariable long todoId,
        @Valid @RequestBody ManagerSaveRequestDto requestDto
    ) {
        return new ResponseEntity<>(
            managerService.saveManager(
                authUser,
                todoId,
                requestDto
            ),
            HttpStatus.OK);
    }

    @GetMapping("/todos/{todoId}/managers")
    public ResponseEntity<List<ManagerResponseDto>> getManagers(@PathVariable long todoId) {
        return new ResponseEntity<>(managerService.getManagers(todoId), HttpStatus.OK);
    }

    @DeleteMapping("/todos/{todoId}/managers/{managerId}")
    public void deleteManager(
        @RequestHeader("Authorization") String bearerToken,
        @PathVariable long todoId,
        @PathVariable long managerId
    ) {
        Claims claims = jwtUtil.extractClaims(bearerToken.substring(7));
        long userId = Long.parseLong(claims.getSubject());
        managerService.deleteManager(userId, todoId, managerId);
    }
}
