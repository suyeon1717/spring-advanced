package org.example.expert.domain.manager.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUserDto;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequestDto;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponseDto;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public ManagerSaveResponse saveManager(
        AuthUserDto authUser,
        long todoId,
        ManagerSaveRequestDto requestDto
    ) {
        Todo todo = todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        // 일정을 만든 유저
        User user = User.fromAuthUser(authUser);

        boolean isNullSafeEqualsTodoAndUser = ObjectUtils.nullSafeEquals(
            user.getId(),
            todo.getUser().getId()
        );
        if (!isNullSafeEqualsTodoAndUser) {
            throw new InvalidRequestException("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.");
        }

        User managerUser = userRepository
            .findById(requestDto.getManagerUserId())
            .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

        boolean isNullSafeEqualsManagerAndUser = ObjectUtils.nullSafeEquals(user.getId(),
            managerUser.getId());
        if (isNullSafeEqualsManagerAndUser) {
            throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
        }

        Manager newManagerUser = new Manager(managerUser, todo);
        Manager savedManagerUser = managerRepository.save(newManagerUser);

        return new ManagerSaveResponse(
            savedManagerUser.getId(),
            new UserResponseDto(managerUser.getId(), managerUser.getEmail())
        );
    }

    public List<ManagerResponse> getManagers(long todoId) {
        Todo todo = todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        List<Manager> managerList = managerRepository.findAllByTodoId(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                manager.getId(),
                new UserResponseDto(user.getId(), user.getEmail())
            ));
        }
        return dtoList;
    }

    @Transactional
    public void deleteManager(long userId, long todoId, long managerId) {
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new InvalidRequestException("User not found"));

        Todo todo = todoRepository
            .findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        boolean isTodoUser = !(todo.getUser() == null || !ObjectUtils.nullSafeEquals(
            user.getId(),
            todo.getUser().getId())
        );
        if (!isTodoUser) {
            throw new InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.");
        }

        Manager manager = managerRepository
            .findById(managerId)
            .orElseThrow(() -> new InvalidRequestException("Manager not found"));

        boolean isManegerUser = ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId());
        if (!isManegerUser) {
            throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.");
        }

        managerRepository.delete(manager);
    }
}
