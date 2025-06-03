# Reactor Context

## 1. Context란 무엇인가?
- **Reactor Context**는 Reactive Streams에서 주 데이터 흐름과 **직교(orthogonal)하는 정보**를 전파하기 위한 기능
  - 하나의 Chain에서 전파되는 공유 메타정보
- **목적**: ThreadLocal의 반응형 대안으로, 스레드 전환에 관계없이 컨텍스트 정보 전파
- **구조**: 키-값 저장소로 작동하며, 각 Subscriber에 연결
  - **전파 방향**: 구독 단계에서 **아래에서 위로(bottom-up)** 전파
    - 같은 operator chain도 구독(subscribe)마다 “다른 context”로 완전히 분리해 실행할 수 있다.
    -  ```kotlin
       fun main() {
            Mono
                .deferContextual { ctx ->
                    println("deferContextual: ${ctx.getOrDefault("msg", "없음")}")
                    Mono.just("result: ${ctx.getOrDefault("msg", "없음")}")
                }
                .contextWrite { ctx ->
                    println("contextWrite1 - 이전: ${ctx.getOrDefault("msg", "없음")}")
                    ctx.put("msg", "First")
                }
                .contextWrite { ctx ->
                    println("contextWrite2 - 이전: ${ctx.getOrDefault("msg", "없음")}")
                    ctx.put("msg", "Second")
                }
                .subscribe { println("subscribe: $it") }
      
                /**
                 * contextWrite2 - 이전: 없음
                 * contextWrite1 - 이전: Second
                 * deferContextual: First
                 * subscribe: result: First
                 */
        }
        ```

## 2. 핵심 특성

### 불변성 (Immutability)
```kotlin
// 새로운 Context 인스턴스 생성 (기존 수정 X)
val newContext = context.put("key", "value")
```

### Context vs ContextView
| 구분 | Context | ContextView |
|------|---------|-------------|
| 용도 | 쓰기/수정 | 읽기 전용 |
| 주요 메서드 | `put()`, `putAll()`, `delete()` | `get()`, `hasKey()` |
| 사용 시점 | `contextWrite` 내부 | 연산자에서 읽기 |

## 3. 사용법

### Context 쓰기
```kotlin
// contextWrite 연산자 사용
mono.contextWrite { ctx -> ctx.put("userId", "12345") }

// 여러 값 설정
mono.contextWrite { ctx -> 
    ctx.put("userId", "12345")
       .put("traceId", "abc-123")
}
```

### Context 읽기
- 읽을 때는 ContextView(읽기전용)로 접근
```kotlin
// Mono.deferContextual 사용
Mono.deferContextual { ctx ->
    val userId = ctx.get<String>("userId")
    Mono.just("Hello $userId")
}

// transformDeferredContextual 사용
flux.transformDeferredContextual { source, ctx ->
    val userId = ctx.get<String>("userId")
    source.map { "[$userId] $it" }
}
```

## 4. 주요 사용 사례

### 요청 범위 데이터 전파
```kotlin
// WebFilter에서 설정
chain.filter(exchange)
    .contextWrite { ctx -> 
        ctx.put("USER_ID", userId)
           .put("TRANSACTION_ID", transactionId)
    }

// 서비스에서 사용
fun processOrder(request: OrderRequest): Mono<Order> {
    return Mono.deferContextual { ctx ->
        val userId = ctx.get<String>("USER_ID")
        val transactionId = ctx.get<String>("TRANSACTION_ID")
        orderService.create(request, userId, transactionId)
    }
}
```

### 인증/권한 정보
```kotlin
// Spring Security와 통합
fun getUserProfile(): Mono<UserProfile> {
    return ReactiveSecurityContextHolder.getContext()
        .map { it.authentication }
        .cast<JwtAuthenticationToken>()
        .flatMap { auth ->
            userRepository.findByUsername(auth.name)
        }
}
```

## 5. Context 우선순위 규칙

```kotlin
// 연산자는 자신 "아래"에 있는 contextWrite만 볼 수 있음
Mono.deferContextual { ctx ->
    Mono.just("Hello ${ctx.get<String>("name")}") 
}
    .contextWrite { ctx -> ctx.put("name", "Reactor") }
    .contextWrite { ctx -> ctx.put("name", "World") }  
    .subscribe{println(it)}

//Hello Reactor
```

### flatMap 내부 격리
```kotlin
flux.flatMap { item ->
    Mono.just(item)
        .contextWrite { ctx -> ctx.put("inner", "value") }  // 내부에만 영향
        .map { /* inner context 사용 가능 */ }
}
// 외부에서는 "inner" 키 접근 불가
```

## 6. ThreadLocal과의 통합

### context-propagation 라이브러리 사용
```kotlin
// 자동 모드 활성화 (Application 시작 시)
Hooks.enableAutomaticContextPropagation()

// 수동 캡처
mono.contextCapture()  // ThreadLocal 값을 Context로 캡처
```

### MDC 로깅 통합
```kotlin
fun <T> withCapturedContext(
  logStatement: (T) -> Unit,
  contextKey: String
): (T) -> Unit {
  // Mono/Flux chain 내에서 context를 MDC에 주입한 뒤 로그
  val capturedContext = Context.capture()
  return { item ->
    val value = capturedContext.getOrEmpty<String>(contextKey)
    value.ifPresent { v ->
      MDC.putCloseable(contextKey, v).use {
        logStatement(item)
      }
    }
    // contextKey가 없으면 평범하게 로그만
    if (!value.isPresent) logStatement(item)
  }
}
```

## 7. 모범 사례

### ✅ DO
- **낮은 키 카디널리티** 유지 (5개 이하 권장)
- **WebFilter에서 초기 설정** (요청 범위 데이터)
- **직교 관심사**에만 사용 (추적, 보안, 로케일 등)
- **성능 측정** 후 context-propagation 사용

### ❌ DON'T
- **비즈니스 데이터 전달**에 Context 사용 금지
- **contextWrite 위치** 잘못 배치 (읽는 연산자보다 위에)
- **가변 객체** Context에 저장 후 수정
- **과도한 contextWrite** 연산 (객체 생성 오버헤드)

## 8. 성능 고려사항
- **Context 자체**: 불변성으로 인한 객체 생성 비용
- **ThreadLocal 통합**: context-propagation 사용 시 성능 영향 (자동이든 수동이든)
- **최적화**: 복잡한 데이터는 단일 키에 전용 객체로 저장
  - Context에 대한 수정에 따른 객체 생성비용은 없지만, 내부에 Map을 넣게되면 다양한 값을 수정 삭제가 가능하기 때문이다.