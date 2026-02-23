# DTO와 VO 정리 (DTO / Value Object)

> 이 문서는 “DTO vs VO”를 **백엔드(Spring/JPA) 실무** 관점에서 다시 정리한 노트다.
> 
> - DTO: 계층/경계 간 데이터 전달(Transport)용
> - VO(Value Object): 값 자체를 표현(도메인/불변/동등성)하는 타입

---

## 1) 먼저 결론: 무엇이 다르고, 왜 중요한가?

- **DTO (Data Transfer Object)**
  - 목적: **경계(boundary) 통과** (Controller ↔ Service, Service ↔ 외부 API, Service ↔ 메시지 등)
  - 특징: 보통 **직렬화/역직렬화**(JSON 등)와 **유효성 검증**(@Valid) 중심
  - 형태: 필드 + 접근자 + (필요 시) 변환 메서드 정도

- **VO (Value Object)**
  - 목적: 도메인에서 의미 있는 “값”을 **타입으로 고정**
  - 특징: 일반적으로 **불변(immutable)**, **동등성(equality)** 이 “식별자”가 아니라 “값”으로 결정
  - 형태: Money, Email, Quantity, Period 같은 타입(도메인 규칙/검증 포함 가능)

둘을 구분하지 않으면 다음 문제가 자주 생긴다.
- DTO에 도메인 규칙이 섞여서 **API 스펙 변경이 도메인에 전파**됨
- VO 대신 primitive/String 남발로 **유효하지 않은 값이 시스템 내부로 침투**(primitive obsession)
- equals/hashCode 기준이 혼재되어 **캐시/Set/Map 동작이 불명확**해짐

---

## 2) DTO (Data Transfer Object)

### 2.1 DTO의 전형적인 사용 위치

```
[Client]
   |
   v
[Controller]  <--- Request DTO / Response DTO
   |
   v
[Service]
   |
   v
[Repository/External]
```

- Request DTO: 입력 검증, 기본값 세팅, API 호환성(필드 추가/폐기) 관리
- Response DTO: 노출 필드 통제(보안), 포맷팅, backward compatibility

### 2.2 DTO에 넣어도 되는 것 / 넣으면 위험한 것

- DTO에 넣어도 되는 것
  - 입력 검증용 어노테이션(@NotNull, @Size 등)
  - 직렬화 규칙(@JsonProperty, @JsonFormat)
  - 변환 메서드(예: `toCommand()`, `toDomain()`) **단, 복잡해지면 Mapper로 분리 권장**

- DTO에 넣으면 위험한 것
  - 도메인 규칙을 확정하는 로직(예: “결제 가능 여부 판단”)
  - JPA 엔티티(`@Entity`)와의 1:1 결합(엔티티를 그대로 API로 노출)

### 2.3 DTO 예시 (Spring Web)

```java
public record CreateOrderRequest(
        @NotNull Long productId,
        @Positive int quantity
) {}

public record OrderResponse(
        Long id,
        String status
) {}
```

- Java `record`는 DTO에 특히 적합하다(불변 + 보일러플레이트 감소).
- 단, 프레임워크/라이브러리 호환(구버전 Jackson 등)이 필요할 수 있으니 프로젝트 버전에 맞춰 선택.

---

## 3) VO (Value Object)

### 3.1 VO의 핵심 성질

- **불변성(immutability)**: 생성 이후 상태가 바뀌지 않음
- **동등성(equality)**: 식별자가 아니라 “값”이 같으면 같은 것으로 취급
- **유효성 보장**: 생성 시점에 제약 조건을 강제(가능하면 “유효한 상태만” 존재)

### 3.2 VO 예시: Email

```java
import java.util.Objects;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern P = Pattern.compile("^[^@]+@[^@]+\\.[^@]+$");

    private final String value;

    private Email(String value) {
        if (value == null || !P.matcher(value).matches()) {
            throw new IllegalArgumentException("invalid email: " + value);
        }
        this.value = value;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email other)) return false;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
```

VO는 “검증/규칙을 객체 내부로 끌어들이는 것”이 핵심이다.

### 3.3 VO와 Entity의 차이(DDD 기준)

- Entity: **식별자(identity)** 로 동일성 판단(예: `OrderId`)
- VO: **값(value)** 으로 동일성 판단(예: `Money(1000, KRW)`)

---

## 4) DTO ↔ VO/도메인 매핑 전략

### 4.1 추천 패턴: Controller DTO → Command → Domain

```
Request DTO
   |
   v
Command(UseCase 입력)  ---->  Domain(Entity/VO)
   |
   v
UseCase/Service
```

- DTO는 “외부 세계”에 가깝고, Domain은 “내부 규칙”에 가깝다.
- 중간에 Command(또는 UseCase Input)를 두면
  - API 스펙 변경이 UseCase까지 침투하는 것을 줄이고
  - 테스트가 쉬워진다.

### 4.2 실무 체크리스트

- [ ] JPA Entity를 API 응답으로 그대로 내보내지 말 것(지연로딩, 순환참조, 보안 필드 등)
- [ ] DTO에서 도메인 규칙을 판단하지 말 것(검증과 규칙은 다름)
- [ ] VO는 생성 시점에 “유효함”을 보장할 것(불변 + 검증)
- [ ] equals/hashCode 기준을 명확히 할 것(VO는 값 기반, Entity는 식별자 기반)

---

## 5) 자주 헷갈리는 용어: VO vs “VO라고 부르는 DTO”

국내 코드베이스에서 흔히 “VO”라고 부르는 것이 사실상 **DTO/POJO**인 경우가 많다.
- 예: `UserVo`라는 이름이지만 mutable + setter 존재 + 단순 데이터 덩어리

이 문서에서의 VO는 DDD의 Value Object 의미에 가깝다.

---

## 참고
- Martin Fowler - Patterns of Enterprise Application Architecture (DTO)
- Eric Evans - Domain-Driven Design (Value Object)
