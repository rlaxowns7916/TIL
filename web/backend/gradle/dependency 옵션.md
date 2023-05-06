# Dependency 옵션

## 1. api
- 의존성을 Runtime, CompileTime에 모두 접근 가능하게 한다.
- 내가 의존하는 프로젝트의 의존성까지 접근 가능하게된다.
  - A -> B (api 의존성) -> C 일 때, A에서도 C에 접근이 가능해진다.

## 2. compileOnly
- Compile시점에만 접근 가능 (코드단에서 직접 참조)
- Reflection과 ServiceLoader와 같은 Runtime시점에 접근 하는 것은 불가능하다.

## 3. runtimeOnly
- Runtime시점에만 접근 가능
- 코드단에서는 접근 불가능하다.

## 4. implementation
- 의존성을 Runtime, CompileTime에 모두 접근 가능하게 한다.
- 내가 의존하는 프로젝트의 의존성은 접근 불가능하다.
  - A -> B (implementation 의존성) -> C 일 때, A에서도 C에 접근이 불가능해진다.

### Compile접근과 Runtime접근의 차이점
- Compile은 소스 코드를 기계어로 번역하고, 문법 오류나 타입 불일치 등을 확인하는 단계
- Runtime은 동적으로 로드되는 Class나 Resource가 필요한 의존성
  - Logging Framework
  - Reflection, ServiceLoader와 같은 Runtime시점 코드를 필요로 하는 기능