# HotSpot JVM vs GraalVM

## HotSpot JVM
- **JIT Compiler 기반**
    - Tier 0: 인터프리터 (느림, 즉시 실행)
    - Tier 1-3: C1 컴파일러 (빠른 컴파일, 기본 최적화)
    - Tier 4: C2 컴파일러 (느린 컴파일, 강력한 최적화)
- 자주 실행되는 코드("핫스팟")를 찾아서 최적화
- 실행 패턴을 보고 맞춤형 최적화 적용
- **WarmUp**이 필요하지만, 완전히 웜업되면 매우 빠름
    - WarmUp 후에는 네이티브 코드보다 빠를 수 있음 (적응형 PGO 효과)
- **ColdStart 문제**가 있다
    - WarmUp에 시간이 걸리기 때문에, 초기 실행 속도가 느림
- **동적 기능 완벽 지원**: 런타임에 자유로운 리플렉션, 동적 클래스 로딩
- **성숙한 GC**: G1, ZGC, Shenandoah 등 다양한 GC 옵션

## GraalVM

#### **GraalVM JIT 모드** (HotSpot 호환 모드)
- **HotSpot JVM + Graal JIT 컴파일러**
- Java로 작성되었다.
- HotSpot의 C2 컴파일러를 **Graal JIT 컴파일러**로 대체
- 기존 HotSpot과 동일한 방식으로 실행하면서 더 나은 성능 제공
- **장점**:
    - 기존 애플리케이션 그대로 사용 가능
    - 동적 기능 완벽 지원 (리플렉션, 동적 프록시 등)
    - Graal 컴파일러의 고급 최적화 활용
    - **다국어 지원**: 동일 런타임에서 Java + JavaScript + Python 등 실행
- **단점**: 여전히 웜업 필요, 메모리 오버헤드 존재

```bash
# GraalVM JIT 모드 실행 (기본 HotSpot 앱과 동일)
java -jar myapp.jar
```

#### **GraalVM Native Image 모드** (AOT 컴파일)
- **AOT (Ahead-Of-Time) 기반**
    - 빌드 시점에 모든 코드를 네이티브 바이너리로 컴파일
    - 런타임에 JIT 컴파일 없이 즉시 실행 가능
- **"클로즈드 월드" 가정**:
    - 빌드 타임에 모든 코드와 의존성이 알려져야 함
    - 정적 분석으로 도달 가능한 코드만 포함
    - 트리 쉐이킹으로 불필요한 코드 제거
    - 최소한의 네이티브 바이너리 생성
- **런타임 제약**:
    - 런타임에 새로운 코드 로딩 불가
    - 모든 동적 기능을 빌드 시점에 명시해야 함
- **성능 특성**:
    - **즉시 시작**: 웜업 시간 0초
    - **Warm Up 없이 즉시 최고 및 일관된 성능**
    - **낮은 메모리 사용량**: JVM 오버헤드 제거
    - **작은 바이너리**: 트리 쉐이킹으로 최소화
    - **클라우드 친화적**: 컨테이너, 서버리스 환경에 최적
- **GC 제약**: 기본 Serial GC (Enterprise Edition에서 G1 지원)

```bash
# Native Image 빌드 및 실행
native-image -jar myapp.jar myapp
./myapp  # 즉시 시작
```

## 📊 전체 비교표

| 측면 | HotSpot JVM | GraalVM JIT 모드                    | GraalVM Native Image |
|------|-------------|-----------------------------------|----------------------|
| **컴파일 시점** | 런타임 (JIT) | 런타임 (Graal JIT)                   | 빌드 타임 (AOT) |
| **시작 시간** | 느림 (2-5초) | 느림 (2-5초)                         | 빠름 (0.1초) |
| **웜업** | 필요 (점진적 최적화) | 필요 (더 나은 최적화)                     | 불필요 (즉시 최고 성능) |
| **최고 성능** | 높음 | 높음 (계산 집약에서는 Hotspot JVM보다 나은 성능) | 높음 (PGO 시 더 높음) |
| **메모리 사용** | 높음 (JIT 오버헤드) | 높음 (JIT 오버헤드)                     | 낮음 (최소 런타임) |
| **바이너리 크기** | 큼 (JAR + JVM) | 큼 (JAR + JVM)                     | 작음 (네이티브 실행파일) |
| **동적 기능** | 완전 지원 | 완전 지원                             | 제한적 (사전 설정 필요) |
| **다국어 지원** | Java만 | Java + JS + Python 등              | 제한적 |
| **GC 옵션** | 다양함 (G1, ZGC 등) | 다양함 (G1, ZGC 등)                   | 제한적 (Serial GC) |

## Runtime 기능 비교

### HotSpot & GraalVM JIT 모드
```kotlin
// 자유자재로 사용 가능
val clazz = Class.forName("com.example.MyClass")
val instance = clazz.getDeclaredConstructor().newInstance()

// Spring의 동적 프록시도 자동으로 처리
@Transactional
@Cacheable("users")
fun getUser(id: Long): User { ... }

// 런타임에 동적 기능 자유롭게 사용
```

### GraalVM Native Image (사전 정의 필요)

#### Reachability Metadata 제공 방법

1. **어노테이션으로 명시**
```kotlin
@RegisterForReflection
data class User(val id: Long, val name: String)
```

2. **설정 파일로 명시**
```json
// reflect-config.json
[
  {
    "name": "com.example.User",
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true
  }
]
```

3. **Tracing Agent로 자동 생성** ⭐
```bash
# HotSpot JVM 앱에서 발생한 모든 비명시적 의존성을 추적하여 
# Reachability Metadata를 자동 생성
java -agentlib:native-image-agent=config-output-dir=META-INF/native-image \
     -jar myapp.jar

# 생성된 메타데이터로 Native Image 빌드
native-image -jar myapp.jar
```

#### Profile-Guided Optimization (PGO)
```bash
# 1. 프로파일링 정보 수집을 위한 빌드
native-image --pgo-instrument -jar myapp.jar myapp-instrumented

# 2. 실제 트래픽과 유사한 테스트로 프로파일 생성
./myapp-instrumented
# (여기서 실제 워크로드 실행)

# 3. 프로파일 정보를 사용한 최적화된 빌드
native-image --pgo=default.iprof -jar myapp.jar myapp-optimized
```

## 선택 기준
### HotSpot JVM
- **장기 실행 엔터프라이즈 애플리케이션**
- **복잡한 동적 기능 사용**
- **기존 레거시 시스템**
- **최고 처리량이 중요한 시스템**

### GraalVM JIT 모드
- **기존 HotSpot 앱의 성능 향상**
- **다국어 애플리케이션** (Java + JavaScript + Python)
- **장기 실행 + 더 나은 성능 원함**
- **기존 앱 그대로 + Graal 최적화 활용**

### GraalVM Native Image
- **빠른 시작이 중요한 환경**
- **메모리 제약이 있는 환경**
- **CLI 도구**

---

### Spring Boot에서 각 모드 사용법
```bash
# 1. HotSpot JVM (기본)
java -jar myapp.jar

# 2. GraalVM JIT 모드
# GraalVM 설치 후 동일하게 실행
export JAVA_HOME=$GRAALVM_HOME
java -jar myapp.jar

# 3. Native Image 모드
./gradlew nativeCompile
./build/native/nativeCompile/myapp
```

