# Lv 4. N+1 문제 개선하기
### ❔ N+1 문제
어떠한 객체를 조회(Querying) 할 시에 발생하는 문제로, 엔티티 컬렉션이나 연관된 엔티티를 Lazy Loading 방식으로 가져올 때 반복적으로 쿼리가 실행되는 것을 의미

### 👀 N+1 문제 예시

<p>
  <img src="https://github.com/user-attachments/assets/6f0a2d2e-723f-46ad-874a-62c517e6ca35" alt="Image 1" width="400"/>
  <img src="https://github.com/user-attachments/assets/1808ad5f-70fb-4395-b25a-8c23d4dfae75" alt="Image 2" width="400"/>
</p>

```java
// TodoService

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
```
```java
// TodoRepository

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    int countById(Long todoId);
}
```

#### 결과는 다음과 같다.
```
Hibernate: 
    /* <criteria> */ select
        t1_0.id,
        t1_0.contents,
        t1_0.created_at,
        t1_0.modified_at,
        t1_0.title,
        t1_0.user_id,
        t1_0.weather 
    from
        todos t1_0 
    order by
        t1_0.modified_at desc 
    limit
        ?
Hibernate: 
    select
        u1_0.id,
        u1_0.created_at,
        u1_0.email,
        u1_0.modified_at,
        u1_0.password,
        u1_0.user_role 
    from
        users u1_0 
    where
        u1_0.id=?
Hibernate: 
    select
        u1_0.id,
        u1_0.created_at,
        u1_0.email,
        u1_0.modified_at,
        u1_0.password,
        u1_0.user_role 
    from
        users u1_0 
    where
        u1_0.id=?

```
- Todo 엔티티에 연결된 User 엔티티를 조회한다. 각 Todo마다 User 데이터를 가져오기 위해 별도의 SELECT 쿼리가 실행된다.
- 만약 10개의 Todo 데이터가 있다고 하면, Todo 데이터 Select 쿼리 1개와 연관된 User 데이터 Select 쿼리 10개가 실행되어 총 11개의 쿼리가 실행된다. → N+1 문제
- ❗️ 하지만 Hibernate는 엔티티를 조회할 때 `Persistence Context(영속성 컨텍스트)`라는 1차 캐시를 사용한다. 즉, 동일한 트랜잭션 내에서 이미 조회된 엔티티는 다시 데이터베이스에서 가져오지 않고, 캐시된 엔티티를 재사용한다.

### 🔧 해결 방안 2가지
#### 1️⃣ `@EntityGraph` 사용
TodoRepository에서 @EntityGraph를 사용하여 Todo 엔티티와 User 엔티티를 한 번의 쿼리로 로드한다.
```java
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @EntityGraph(attributePaths = {"user"})
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    int countById(Long todoId);
}
```
#### 2️⃣ JPQL로 Fetch Join 사용
```java
public interface TodoRepository extends JpaRepository<Todo, Long> {

    @Query("SELECT t FROM Todo t JOIN FETCH t.user ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    int countById(Long todoId);
}
```
#### `결과)` 다음과 같이 쿼리가 1번만 실행되는 것을 알 수 있다.
```
Hibernate: 
    /* SELECT
        t 
    FROM
        Todo t 
    JOIN
        
    FETCH
        t.user 
    ORDER BY
        t.modifiedAt DESC */ select
            t1_0.id,
            t1_0.contents,
            t1_0.created_at,
            t1_0.modified_at,
            t1_0.title,
            t1_0.user_id,
            u1_0.id,
            u1_0.created_at,
            u1_0.email,
            u1_0.modified_at,
            u1_0.password,
            u1_0.user_role,
            t1_0.weather 
        from
            todos t1_0 
        join
            users u1_0 
                on u1_0.id=t1_0.user_id 
        order by
            t1_0.modified_at desc 
        limit
            ?
```

### ✍🏼 수정 부분
#### `getTodos` , `getComments` , `getMembers` 
