# AOT(Ahead-Of-Time) Compiler 

## 개요

### 핵심 개념
- **실행 전**에 미리 네이티브 코드로 컴파일하는 기술
- JIT과 반대 개념 (Just-In-Time vs Ahead-Of-Time)
- Java 9부터 실험적 도입 (`jaotc`), GraalVM으로 본격화

### JIT vs AOT 상세 비교
| 구분          | JIT Compiler          | AOT Compiler        |
|-------------|-----------------------|---------------------|
| **컴파일 시점**  | 런타임 (Hot Method 감지 시) | 빌드 타임               |
| **시작 속도**   | 느림 (Warm-up 필요)       | 빠름 (즉시 실행)          |
| **메모리 사용**  | 높음 (JVM + JIT 데이터 구조) | 낮음 (네이티브만)          |
| **최적화**     | 런타임 프로파일 기반 동적 최적화    | 정적 최적화만             |
| **Peak 성능** | Warm-up 후 최고          | 일정하지만 JIT보다 낮을 수 있음 |
| **빌드 성능**   | 상대적으로 빠름              | 상대적으로 느림            |
| **배포 크기**   | 작음 (JAR)              | 큼 (실행 파일)           |
| **플랫폼 독립성** | 독립적 (Bytecode)        | 독립적 (AOT Cache)     |
| **동적 기능**   | 완전 지원                 | 제한적 (명시적 구성 필요)     |

## GraalVM Native Image

### 동작 원리
```
[Java/Kotlin] → [Bytecode] → [정적 분석] → [Native Code]
                                  ↓
                          [Reachability 분석]
                          [Closed World 가정]
```

### Closed World Assumption
- 모든 코드가 빌드 시점에 알려져 있다고 가정
- 동적 클래스 로딩, 리플렉션 등은 명시적 구성 필요
- Tree Shaking으로 사용하지 않는 코드 제거

### 제약사항과 해결방법

#### 1. 리플렉션
```json
// reflect-config.json
[{
    "name": "com.example.MyClass",
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
}]
```

#### 2. 리소스 접근
```json
// resource-config.json
{
    "resources": {
        "includes": [
            {"pattern": "config\\.properties"}
        ]
    }
}
```

#### 3. 동적 프록시
```json
// proxy-config.json
[{
    "interfaces": ["com.example.MyInterface"]
}]
```

## 연관 기술
| **특성** | **Project Leyden** | **GraalVM Native Image** | **CRaC (Coordinated Restore at Checkpoint)** |
| --- | --- | --- | --- |
| **주요 목표** | 표준 JVM의 시작 시간, 최고 성능 도달 시간, 메모리 공간 개선| 최소한의 리소스로 즉시 시작하는 독립형 네이티브 실행 파일 생성 | 워밍업된 JVM 상태를 복원하여 거의 즉각적인 시작 달성 |
| **핵심 메커니즘** | AOT 클래스 로딩/링킹, AOT 객체 캐싱, 향후 AOT 코드 컴파일 (JVM 내) | 전체 애플리케이션 AOT 컴파일, Substrate VM에서 실행 | 실행 중인 JVM의 스냅샷 생성 및 복원 (체크포인트/리스토어) |
| **시작 속도 향상** | 상당한 개선 | 매우 빠름 (밀리초 단위, 최대 100배) | 거의 즉각적 |
| **메모리 공간 감소** | 중간 정도 (JIT와 전체 네이티브 사이)  | 매우 작음 (JVM의 일부)  | 시작 시 메모리 압력 감소 , 복원된 상태에 따라 다름 |
| **최고 성능 영향** | JIT와 함께 작동하여 잠재적으로 유지 또는 향상 가능  | JIT의 동적 최적화 부재로 인해 전통적인 JVM보다 낮을 수 있음 | JIT 최적화 상태를 보존하므로 최고 성능 유지 가능 |
| **동적 Java 호환성** | 선택적 제한, 기존 JVM 동적성 최대한 유지 목표  | 제한적, 리플렉션/JNI 등에 대한 명시적 구성 필요 (폐쇄된 세계) | 애플리케이션이 리소스 관리 API를 구현해야 함 |
| **OS 의존성** | 플랫폼 독립적 (OpenJDK 기능) | 빌드된 실행 파일은 플랫폼 종속적  | 현재 Linux (CRIU) 의존적 |
| **상태 저장/비저장** | 주로 비저장 (AOT 캐시는 데이터) | 비저장 (실행 파일) | 상태 저장 (메모리 스냅샷) |
| **애플리케이션 코드 변경** | JEP 483은 불필요, 향후 제약 조건은 옵트인 가능성 | 일반적으로 불필요하나, 동적 기능 사용 시 구성 필요 | `Resource` 인터페이스 구현 필요 |
| **주요 사용 사례** | 일반 Java 앱, 클라우드 네이티브, 시작/메모리 공간 개선이 필요한 곳 | CLI, 서버리스, 극도의 시작/메모리 공간 최적화가 필요한 마이크로서비스 | 빈번한 스케일 아웃, 워밍업된 상태의 빠른 복원이 중요한 경우 |


### Leyden이 적합한 경우
- 일반 엔터프라이즈 애플리케이션
- 동적 기능이 필요하면서도 빠른 시작 원하는 경우
- 표준 JVM 생태계 유지가 중요한 경우
- 점진적 성능 개선을 원하는 경우

### Layen AOT Cache
- 사전 처리된 메타데이터와 객체 그래프
- JVM이 이해할 수 있는 중간 형식
  - JVM이 참조하여, Skip, Reuse, Hint에 활용한다.
- Fallback 매커니즘이 존재한다.
  - Cache에 없으면 기존방식으로 동작한다.
  - 완전한 하위호환성을 제공하기 위함이다.

### GraalVM Native Image가 적합한 경우
- CLI 도구
- 극도의 메모리 효율이 필요한 서버리스
- Closed World 제약을 수용할 수 있는 경우
- JIT 없이도 충분한 성능인 경우

### CRaC가 적합한 경우
- 복잡한 초기화가 있는 애플리케이션
- 동일 상태로 빈번한 재시작이 필요한 경우
- Linux 환경으로 제한되어도 괜찮은 경우


## 정리

1. **AOT는 JIT의 대체가 아닌 보완**: 각각의 장단점을 이해하고 적절히 선택
2. **Project Leyden은 점진적 개선**: 기존 코드 대부분 그대로 사용하면서 성능 향상
3. **Closed World vs Open World**: 완전한 정적 분석 vs 동적성 유지의 균형
4. **생태계 전체의 움직임**: JVM, 프레임워크, 도구가 함께 진화
5. 목적
   - **하이브리드 실행 모델**: AOT + JIT 최적 조합
   - **유연한 성능 프로파일**: 애플리케이션별 최적화 선택
   - **클라우드 비용 절감**: 빠른 시작과 낮은 메모리로 인프라 비용 감소