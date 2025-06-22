# Java Agent

- **JVM 수준에서 바이트코드를 조작**하여 런타임 동작을 변경하는 메커니즘
    - Class파일을 변경하는 것이 아닌, JVM이 로드하는 Class의 바이트코드를 변경
- **소스 코드 수정 없이** 횡단 관심사(logging, monitoring, security 등)를 구현
- **Java 5부터 도입**, `java.lang.instrument` 패키지의 **Instrumentation API** 기반

## 주요특징

- 프로덕션에서 재배포 없이 문제 진단/수정
- 서드파티 라이브러리 내부 동작 제어
- 비즈니스 로직과 모니터링 로직 완전 분리
- APM, 코드 커버리지, 핫스와핑 등 고급 기능 구현

---

## premain vs agentmain

### premain (정적 로딩)

```bash
java -javaagent:agent.jar MyApp
```

| 특징        | 설명                                 |
|-----------|------------------------------------|
| **실행 시점** | JVM 시작 시, main() 실행 전              |
| **매니페스트** | `Premain-Class: com.example.Agent` |
| **적용 범위** | 모든 클래스 로딩 시점에 개입 가능                |
| **주요 용도** | APM, 전역 모니터링, 시스템 전반 계측            |

### agentmain (동적 로딩)

```java
// Java Attach API 사용
VirtualMachine vm = VirtualMachine.attach(pid);
vm.

loadAgent("agent.jar");
```

| 특징        | 설명                               |
|-----------|----------------------------------|
| **실행 시점** | 실행 중인 JVM에 동적 연결                 |
| **매니페스트** | `Agent-Class: com.example.Agent` |
| **적용 범위** | 이미 로드된 클래스 재변환 가능                |
| **주요 용도** | 런타임 디버깅, 긴급 패치, 문제 진단            |

---

## 핵심 원리

### ClassFileTransformer 작동 원리

```java
public class MyAgent {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(
                    ClassLoader loader, String className,
                    Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) {

                // 바이트코드 조작 로직
                return modifiedBytecode;
            }
        });
    }
}
```

### 핵심 Instrumentation API

| 메서드                     | 용도             |
|-------------------------|----------------|
| `addTransformer()`      | 클래스 변환기 등록     |
| `retransformClasses()`  | 이미 로드된 클래스 재변환 |
| `redefineClasses()`     | 클래스 완전 재정의     |
| `getAllLoadedClasses()` | 로드된 모든 클래스 조회  |

---

## 바이트코드 조작 Library

| 항목                | ASM                 | CGLIB             | Javassist       | ByteBuddy          |
|-------------------|---------------------|-------------------|-----------------|--------------------|
| **API 수준**        | 저수준 (Opcode 직접)     | 고수준 (프록시 중심)      | 고수준 (소스/바이트 코드) | 고수준 (DSL)          |
| **학습 곡선**         | 매우 어려움              | 보통                | (쉬움)            | (보통)               |
| **바이트코드 지식 필요**   | 필수                  | 선택적               | 최소              | 불필요                |
| **타입 안전성**        | X                   | 제한적               | 제한적             | 강력                 |
| **런타임 의존성**       | X 없음                | 필요                | 필요              | 선택적                |
| **클래스 생성**        | O                   | O                 | O               | O                  |
| **클래스 수정**        | O                   | X                 | O               | O                  |
| **동적 프록시**        | 수동 구현               | O 특화              | O               | O                  |
| **Java Agent 지원** | O                   | X                 | O               | O 특화               |
| **제네릭 지원**        | O                   | 제한적               | 제한적             | O                  |
| **코드 가독성**        | 낮음                  | 높음                | 높음              | 매우 높음              |
| **주요 사용처**        | 프레임워크 핵심            | Spring, Hibernate | 간단한 계측          | APM, Mocking       |


## vs Spring AOP
| 특징 | Spring AOP | Java Agent |
| --- | --- | --- |
| **작동 방식** | 프록시 기반 (JDK Dynamic Proxy, CGLIB) | 클래스 바이트코드 직접 조작 (ClassFileTransformer) |
| **적용 시점** | 런타임 (Bean 생성 시) | JVM 시작 시 (premain) 또는 런타임 동적 연결 (agentmain) |
| **적용 대상** | Spring Bean의 인스턴스 메서드 (외부 호출만) | 모든 클래스의 메서드 (Bean/Non-Bean, 인스턴스/정적, 내부/외부 호출 모두) |
| **소스 코드 변경** | 없음 (프레임워크가 처리) | 없음 (바이트코드 수정) |
| **프레임워크 의존성** | Spring Framework에 강하게 의존 | JVM Instrumentation API에 의존 (프레임워크 독립적) |
| **주요 활용** | 트랜잭션, 캐싱, 보안(Spring Security), 로깅 등 Spring 애플리케이션 내 AOP | APM, 코드 커버리지, 런타임 디버깅, 라이브러리/프레임워크 내부 계측 |