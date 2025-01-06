## 1️⃣ 테스트 연습
### 오류 원인 
👉🏻 rawPassword와 encodedPassword 순서가 잘못되었다.
`PasswordEncoder.matches(String rawPassword, String encodedPassword)`
```java
@ExtendWith(SpringExtension.class)
class PasswordEncoderTest {

    @InjectMocks
    private PasswordEncoder passwordEncoder;

    @Test
    void matches_메서드가_정상적으로_동작한다() {
        // given
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
//        boolean matches = passwordEncoder.matches(encodedPassword, rawPassword); // 수정 전
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword); // 수정 후

        // then
        assertTrue(matches);
    }
}
```

## 2️⃣ -1. 테스트 연습2 1번 케이스
### 기존 테스트 코드
```java
@Test
public void manager_목록_조회_시_Todo가_없다면_NPE_에러를_던진다() {
// given
long todoId = 1L;
given(todoRepository.findById(todoId)).willReturn(Optional.empty());

    // when & then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class, 
			    () -> managerService.getManagers(todoId));
    assertEquals("Manager not found", exception.getMessage());
}
```
### ManagerService.java
```java
@Transactional
public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
    // 일정을 만든 유저
    User user = User.fromAuthUser(authUser);
    Todo todo = todoRepository.findById(todoId)
            .orElseThrow(() -> new InvalidRequestException("Todo not found"));

    ...

    return new ManagerSaveResponse(
            savedManagerUser.getId(),
            new UserResponse(managerUser.getId(), managerUser.getEmail())
    );
}
```
### 오류 원인
👉🏻 `new InvalidRequestException("Todo not found"))` 에러 메시지가 일치해야 한다.

### 수정 테스트 코드
```java
@Test
public void manager_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다() {
// given
long todoId = 1L;
given(todoRepository.findById(todoId)).willReturn(Optional.empty());

    // when & then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class,
        () -> managerService.getManagers(todoId));
    assertEquals("Todo not found", exception.getMessage());
}
```

### 테스트 코드 메서드 명도 수정하였다. (이렇게 길어도 될까..?)
👉🏻 `manager_목록_조회_시_Todo가_없다면_InvalidRequestException_에러를_던진다()`


## 2️⃣ -2. 테스트 연습2 2번 케이스
### 기존 테스트 코드
```java
@Test
public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
// given
long todoId = 1;
CommentSaveRequest request = new CommentSaveRequest("contents");
AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

    given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

    // when
    ServerException exception = assertThrows(ServerException.class, () -> {
        commentService.saveComment(authUser, todoId, request);
    });

    // then
    assertEquals("Todo not found", exception.getMessage());
}
```
### 기존 테스트 결과
```java
Unexpected exception type thrown, expected: <org.example.expert.domain.common.exception.ServerException> but was: <org.example.expert.domain.common.exception.InvalidRequestException>
Expected :class org.example.expert.domain.common.exception.ServerException
Actual   :class org.example.expert.domain.common.exception.InvalidRequestException
```

### 오류 원인
👉🏻 `InvalidRequestException` 이 아닌 `ServerException` 으로 에러처리가 되고 있다. 

### 수정 테스트 코드
```java
@Test
public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
// given
long todoId = 1;
CommentSaveRequestDto request = new CommentSaveRequestDto("contents");
AuthUserDto authUser = new AuthUserDto(1L, "email", UserRole.USER);

    given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

    // when
    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
        commentService.saveComment(authUser, todoId, request);
    });

    // then
    assertEquals("Todo not found", exception.getMessage());
}
```

## 2️⃣ -3. 테스트 연습2 3번 케이스
### 기존 테스트 코드
```java
void todo의_user가_null인_경우_예외가_발생한다() {
// given
AuthUserDto authUser = new AuthUserDto(1L, "a@a.com", UserRole.USER);
long todoId = 1L;
long managerUserId = 2L;

    Todo todo = new Todo();
    ReflectionTestUtils.setField(todo, "user", null);

    ManagerSaveRequestDto managerSaveRequest = new ManagerSaveRequestDto(managerUserId);

    given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

    // when & then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
        managerService.saveManager(authUser, todoId, managerSaveRequest)
    );

    assertEquals("담당자를 등록하려고 하는 유저와 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
}
```
### 기존 테스트 결과
```java
Expected :class org.example.expert.domain.common.exception.InvalidRequestException
Actual   :class java.lang.NullPointerException
```
### 오류 원인
👉🏻 ManagerService의 saveManager() 메소드 내의 `todo.getUser()` 에서 NPE 가 발생한다.
```java
boolean isNullSafeEqualsTodoAndUser = ObjectUtils.nullSafeEquals(
    user.getId(),
    todo.getUser().getId()
);
if (!isNullSafeEqualsTodoAndUser) {
    throw new InvalidRequestException("담당자를 등록하려고 하는 유저와 일정을 만든 유저가 유효하지 않습니다.");
}
```

### ManagerService의 saveManager() 메소드 수정
```java
public ManagerSaveResponseDto saveManager(
    AuthUserDto authUser,
    long todoId,
    ManagerSaveRequestDto requestDto
    ) {
    Todo todo = todoRepository
    .findById(todoId)
    .orElseThrow(() -> new InvalidRequestException("Todo not found"));
    
    
        // lv 5-2 3번 케이스에 의해 추가, null 에러 처리
        if (todo.getUser() == null) {
            throw new InvalidRequestException("Todo's User not found");
        }
    
        // 일정을 만든 유저
        User user = User.fromAuthUser(authUser);
    
        boolean isNullSafeEqualsTodoAndUser = ObjectUtils.nullSafeEquals(
            user.getId(),
            todo.getUser().getId()
        );
        if (!isNullSafeEqualsTodoAndUser) {
            throw new InvalidRequestException("담당자를 등록하려고 하는 유저와 일정을 만든 유저가 유효하지 않습니다.");
        }
    
        ...생략...
        
        return new ManagerSaveResponseDto(
            savedManagerUser.getId(),
            new UserResponseDto(managerUser.getId(), managerUser.getEmail())
        );
```

## 3️⃣ Interceptor와 AOP를 활용한 API 로깅