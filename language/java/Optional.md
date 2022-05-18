# Optional
- NPE를 간편하게 해결하기 위해서 만들어졌다.
- 값이 NULL 일 수도 있다는 것을 나타내는 것이다.
- NULL 일 경우의 편의 메소드를 제공함으로써 NPE를 예방한다.

### 사용법
```java
public Optional<T> findById(Long id);
```

### 1. isPresent
- value가 null이 아니라면 True를 리턴한다.
- value가 null이라면 false를 리턴한다.

### 2. ifPresent
- value가 존재한다면 수행할 작업을 명시한다.

### 3. orElse
- 제네릭인 객체 <T>를 인자로 넣어준다.
- 인자 그 자체이기 때문에, NULL 여부와 상관없이 리턴된다.

### 4. orElseGet
- 인자로 Supplier를 받는다.
- null일 때만 호출된다.

### 4. orElseThrow
- value가 null이라면 Throw할 Error를 명시한다.
