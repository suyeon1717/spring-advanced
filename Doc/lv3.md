#  Lv 3. 추가 기능 개선
## 1️⃣ Early Return
### 조건에 맞지 않는 경우 즉시 리턴하여, 불필요한 로직의 실행을 방지하고 성능을 향상시킨다.
#### - 수정 전
```java
String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

UserRole userRole = UserRole.of(signupRequest.getUserRole());

if (userRepository.existsByEmail(signupRequest.getEmail())) {
    throw new InvalidRequestException("이미 존재하는 이메일입니다.");
}
```
#### - 수정 후
```java
if (userRepository.existsByEmail(signupRequest.getEmail())) {
    throw new InvalidRequestException("이미 존재하는 이메일입니다.");
}

String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

UserRole userRole = UserRole.of(signupRequest.getUserRole());
```

## 2️⃣ 불필요한 if-else 피하기
#### - 수정 전
```java
WeatherDto[] weatherArray = responseEntity.getBody();
if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
    throw new ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.getStatusCode());
} else {
    if (weatherArray == null || weatherArray.length == 0) {
        throw new ServerException("날씨 데이터가 없습니다.");
    }
}
```
#### - `if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) `에 해당될 경우, 에러 예외처리를 하고 있다. 불필요한 else 를 제거하고, if 다음 if를 배치하여도 if-else와 같은 동작을 한다.
#### - 수정 후
```java
WeatherDto[] weatherArray = responseEntity.getBody();
if (!HttpStatus.OK.equals(responseEntity.getStatusCode())) {
    throw new ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.getStatusCode());
}
if (weatherArray == null || weatherArray.length == 0) {
    throw new ServerException("날씨 데이터가 없습니다.");
}
```

## 3️⃣ 코드 클린업
#### - 불필요한 주석 제거, 코드 포맷팅, 일관된 네이밍 컨벤션 적용
#### - 팀 내에서 정한 코드 컨벤션 규칙 적용
#### - 기본적으로 google 코드 컨벤션 적용
#### - lv3 브랜치 참고

## 4️⃣ 중복 코드 제거
#### - 중복 코드 식별
#### - 메서드로 추출 ( 단순 private 메소드로의 추출 x)
#### - 개인적으로 제일 어려웠다.. 😂
#### - 아이디로 엔티티를 가져오면서 에러처리까지 해주는 코드가 중복되기도 하고 여러 서비스에서 쓰이고 있어 메서드로 추출하였다.
#### - 중복 코드 (비즈니스 로직을 수행하는 코드를 메서드로 추출해도 괜찮은지 의문..)
* ManagerService
```java
@Transactional
public ManagerSaveResponseDto saveManager(
  ...
) {
    Todo todo = todoRepository
        .findById(todoId)
        .orElseThrow(() -> new InvalidRequestException("Todo not found"));
	
		...

    User managerUser = userRepository
        .findById(requestDto.getManagerUserId())
        .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

    return new ManagerSaveResponseDto(
        savedManagerUser.getId(),
        new UserResponseDto(managerUser.getId(), managerUser.getEmail())
    );
}

public List<ManagerResponseDto> getManagers(long todoId) {
    Todo todo = todoRepository
        .findById(todoId)
        .orElseThrow(() -> new InvalidRequestException("Todo not found"));

    ...

    return managerResponseDtoList;
}

@Transactional
public void deleteManager(long userId, long todoId, long managerId) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new InvalidRequestException("User not found"));

    Todo todo = todoRepository
        .findById(todoId)
        .orElseThrow(() -> new InvalidRequestException("Todo not found"));

    ...

    Manager manager = managerRepository
        .findById(managerId)
        .orElseThrow(() -> new InvalidRequestException("Manager not found"));

    ...

    managerRepository.delete(manager);
}

```

* UserService
```java
public UserResponseDto getUser(long userId) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new InvalidRequestException("User not found"));

    return new UserResponseDto(user.getId(), user.getEmail());
}

@Transactional
public void changePassword(long userId, UserChangePasswordRequestDto requestDto) {
    ...

    User user = userRepository
        .findById(userId)
        .orElseThrow(() -> new InvalidRequestException("User not found"));

    ...

    user.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));
}
```

* CommentService
```java
public CommentSaveResponseDto saveComment(
    ...
) {
    Todo todo = todoRepository.
        findById(todoId).
        orElseThrow(() -> new InvalidRequestException("Todo not found"));
	...

    return new CommentSaveResponseDto(
        savedComment.getId(),
        savedComment.getContents(),
        new UserResponseDto(user.getId(), user.getEmail())
    );
}   
```
#### - 클래스로 공통 메서드를 관리
* CommonService
```java
private <T> T findEntityById(JpaRepository<T, Long> repository, Long id, String errorMessage) {
    return repository.findById(id).orElseThrow(() -> new InvalidRequestException(errorMessage));
}
```

#### - 결과
```java
@Transactional
public ManagerSaveResponseDto saveManager(
  ...
) {
    Todo todo = commonService.findEntityById(
        todoRepository,
        todoId,
        "Todo not found"
    );

		...
    
        User managerUser = commonService.findEntityById(
        userRepository,
        requestDto.getManagerUserId(),
        "등록하려고 하는 담당자 유저가 존재하지 않습니다."
    );
    
    ...

    return new ManagerSaveResponseDto(
        savedManagerUser.getId(),
        new UserResponseDto(managerUser.getId(), managerUser.getEmail())
    );
}

public List<ManagerResponseDto> getManagers(long todoId) {
    Todo todo = commonService.findEntityById(
            todoRepository,
            todoId,
            "Todo not found"
        );

    ...
    
    return managerResponseDtoList;
}

@Transactional
public void deleteManager(long userId, long todoId, long managerId) {
    User user = commonService.findEntityById(
        userRepository,
        userId,
        "User not found"
    );

    Todo todo = commonService.findEntityById(
        todoRepository,
        todoId,
        "Todo not found"
    );

    ...

    Manager manager = commonService.findEntityById(
        managerRepository,
        managerId,
        "Manager not found"
    );

    ...

    managerRepository.delete(manager);
}

```