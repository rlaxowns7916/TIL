# JIT(Just-In-Time) Compiler

##  개요
- Java의 성능을 향상시키는 **동적 컴파일러**
- Java의 인터프리터 방식을 보완하는 메커니즘
- **Hot Method** 단위로 컴파일하고 캐싱
- JVM 시작 시 자동으로 실행됨

##  Java의 동작 과정
### Hybrid 언어 특성
```
[Java 코드] → (javac) → [.class 파일] → (JVM) → [기계어]
                ↑                          ↑
            컴파일 방식              인터프리터 방식
```

### JIT Compiler의 역할
- 자주 사용되는 코드를 **기계어로 미리 컴파일**
  - 더이상 Interpreter를 거치지 않음
- 컴파일된 코드를 **Code Cache에 저장**
  - CodeCache가 가득차면 JIT Compiler가 비활성화되고, 인터프리터 모드로만 실행된다.
- 재실행 시 캐싱된 기계어를 바로 사용

## JVM Warm-Up
### 개념
- JIT Compiler는 캐시이므로 초기에는 비어있음
- 의도적으로 로직을 실행시켜 최적화 수행
- Warm-Up 정도에 따라 최적화 강도 조절 가능

### 컴파일 레벨
| Level | 컴파일러 | 설명 |
|-------|----------|------|
| 0 | Interpreter | 기본 인터프리팅, 프로파일링 정보 수집 |
| 1 | C1 | 간단한 최적화, 빠른 컴파일 |
| 2 | C1 | invocation & backedge counters 추가 |
| 3 | C1 | 전체 프로파일링 |
| 4 | C2 | 공격적인 최적화, 느린 컴파일 |
- 순차적으로 WarmUp이 되며, 임계치를 넘어야 다음 Level로 넘어간다.
  - 즉, C2로 Full 최적화 하기 위해서는, 충분한 수준의 Warm-Up이 필요하다.

## 컴파일러 상세

### C1 Compiler (Client Compiler)
**특징:**
- 빠른 컴파일 속도
- 기본적인 최적화 수행
- Level 1~3에서 동작

**최적화 기법:**
- **Method Inlining**: 작은 메서드를 호출 지점에 직접 삽입
- **Dead Code Elimination**: 사용되지 않는 코드 제거
- **Constant Folding**: 컴파일 시점에 상수 계산

### C2 Compiler (Server Compiler)
**특징:**
- 느린 컴파일 속도, 높은 최적화
- Level 4에서 동작
- 장기 실행 애플리케이션에 적합

**고급 최적화 기법:**
- **Escape Analysis**: 객체가 메서드 밖으로 나가지 않으면 스택 할당
- **Loop Unrolling**: 루프 펼치기로 분기 예측 비용 감소
- **Vectorization**: SIMD 명령어 활용
- **Branch Prediction**: 분기 예측 최적화

##  컴파일 임계값

### 기본 임계값
```bash
-XX:CompileThreshold=10000  # 메서드 호출 10,000번 시 컴파일
```

### 프로파일링 정보
JIT가 수집하는 정보:
- 메서드 호출 횟수
- 루프 반복 횟수 (backedge counter)
- 타입 정보 (실제 사용되는 구체 타입)
- 분기 통계 (어느 분기가 자주 실행되는지)

## 특수 기능

### OSR (On-Stack Replacement)
```kotlin
// 실행 중인 메서드도 최적화 가능
fun longRunningMethod() {
    for (i in 1..1_000_000) {
        // 루프 실행 중에도 최적화 적용
        processData(i)
    }
}
```

### Deoptimization
```kotlin
// 최적화가 취소되는 경우
open class Animal { 
    open fun sound() = "..." 
}

class Dog : Animal() { 
    override fun sound() = "Bark" 
}

// Animal.sound()가 최적화되었다가
// Dog 인스턴스 등장 시 deoptimization 발생
```

## Code Cache
### 개념
- JIT 컴파일된 네이티브 코드 저장소
- 메모리 영역에 위치
- 크기 제한 존재

### 관련 옵션
```bash
-XX:InitialCodeCacheSize=32m     # 초기 크기
-XX:ReservedCodeCacheSize=240m   # 최대 크기
-XX:CodeCacheExpansionSize=64k   # 확장 단위
```

##  JVM 옵션

### 컴파일러 모드
```bash
-client                    # C1 compiler만 사용
-server                    # C2 compiler 사용
-XX:+TieredCompilation    # C1 + C2 (기본값)
-XX:-TieredCompilation    # Tiered Compilation 비활성화
```

### 모니터링 옵션
```bash
-XX:+PrintCompilation                        # JIT 컴파일 로그
-XX:+UnlockDiagnosticVMOptions              # 진단 옵션 활성화
-XX:+LogCompilation                         # 상세 컴파일 로그
-XX:+PrintInlining                          # 인라이닝 정보
-XX:+TraceClassLoading                      # 클래스 로딩 추적
```

### 튜닝 옵션
```bash
-XX:CompileThreshold=1000                   # 컴파일 임계값 조정
-XX:OnStackReplacePercentage=140           # OSR 임계값
-XX:MaxInlineSize=35                       # 인라이닝 최대 크기
-XX:FreqInlineSize=325                     # 자주 호출되는 메서드 인라이닝 크기
```


##  주의사항
- 너무 많은 다형성은 최적화를 방해
- 리플렉션 사용은 JIT 최적화 제한
- Code Cache가 가득 차면 컴파일 중단
- 메서드는 작게 유지 (인라이닝 가능하도록)
- 핫스팟 코드는 단순하게 작성
