# ClassLoader
- JVM이 특정 플랫폼에 종속적이지 않게하는 핵심
- JVM의 핵심 요소중에 하나이다.
  - `ClassLoader`
  - Execution Engine
  - MemoryAreas
- **Compile된 Java ByteCode를* Runtime에 Memory에 올리는 역할을 한다*
  - 한번에 올리지않고, 필요한 시점에만 Load 하여 메모리 효율성을 높인다.

## 핵심 기능
- .class(ByteCode)파일을 읽어, `Class`객체를 생성하고, JVM Runtime Memory에 로드한다.
- **JVM은 Runtime에 Class가 동적으로 로드되기 전에, Method, Field, 상속 관계 등에 대한 사정정보를 가지고 있지 않다.**

## Class Loading 과정

### [1] Loading
- ClassLoader가 .class 파일을 읽어, Class 객체를 생성한다.
- Binary Data에는 Class Metadata가 포함되어있다.
  - FQDN, Class/Interface/Enum 여부, Method 및 변수 정보, ...
- **Method 영역에 저장한다.**
- **Loading이 완료되면 Class 객체가 Heap 영역에 저장된다.**
- 아래와 같은 상황이 Loading을 유발시킨다.
  - static method 호출
  - static field 접근
  - instance 생성
  - reflection 호출

### [2] Linking
- Load된 Class를 실행가능한 상태로 변경하는 과정이며, JVM의 Runtime 환경과 통합된다.
- 아래와 같은 단계로 이루어진다.
  1. **Verification**
     - Class 파일의 구조와 내용이 JVM 스펙에 맞는지 검사
       - 검사에 실패하면 `java.lang.VerifyError` 발생
  2. **Preparation**
     - Class의 static 변수를 초기화한다.
       - 상수의 경우에는 Compile Time에 이미 처리된다.
     - 이 과정에서 JVM은 기본값으로 초기화한다. (null, 0, false 등)
       - 숫자타입: 0
       - boolean: false
       - 참조타입: null
  3. **Resolution**
     - Class의 심볼릭 레퍼런스를 실제 참조로 변환한다.
     - JVM 구현체에 따라 eager 또는 lazy하게 수행됨 (개발자가 선택하는 것이 아님)
     - 클래스, 인터페이스, 필드, 메서드 레퍼런스를 실제 메모리 주소로 해석

### [3] Initialization
- Class가 실제로 사용되기 전에 초기화하는 단계
- 이 단계에서 static 초기화 블록과 static 변수들이 실행된다. (사용자가 정의된 최종 값이 할당)

| 단계/하위 단계 | 주요 활동/목적 | 관련 메모리 영역 |
| --- | --- | --- |
| **로딩 (Loading)** | `.class` 파일(바이트코드)을 읽고 바이너리 데이터 생성 및 저장. 클래스 메타데이터(FQCN, 타입, 메서드/변수 정보) 포함. `Class` 객체를 힙에 생성. | 메서드 영역(Metaspace), 힙 영역 |
| **링크 (Linking)** | 로드된 클래스를 실행 가능한 상태로 준비 | - |
| - 검증 (Verification) | `.class` 파일의 바이트코드 유효성 및 무결성 검사 | - |
| - 준비 (Preparation) | 정적 변수 및 데이터 구조를 위한 메모리 할당. 정적 변수 기본값 초기화 | - |
| - 분석 (Resolution) | 심볼릭 레퍼런스를 다이렉트 레퍼런스(실제 메모리 주소)로 교체. (선택적 단계) | 런타임 상수 풀 (메서드 영역 내) |
| **초기화 (Initialization)** | 클래스의 정적 초기화 블록 실행 및 정적 필드에 최종 값 할당 | - |

---
## ClassLoader 원칙

### [1] 위임 원칙
- 클래스 로더가 클래스 로딩 요청을 받으면, 먼저 요청을 자신의 부모 클래스 로더에게 위임
- "부모 우선(parent-first)" 전략으로 부트스트랩 클래스 로더에 도달할 때까지 계층 구조를 따라 계속됨
- 부모(및 그 부모들)가 클래스를 찾거나 로드할 수 없는 경우에만 원래의 클래스 로더가 직접 클래스를 로드하려고 시도
- 어떤 클래스 로더도 클래스를 찾지 못하면 `ClassNotFoundException` 발생

### [2] 가시 범위 원칙
- 부모 클래스 로더에 의해 로드된 클래스는 자식 클래스 로더에게 보임
- 자식 클래스 로더에 의해 로드된 클래스는 부모 클래스 로더에게 보이지 않음

### 3. 유일성 원칙 (Uniqueness Property)
- 클래스 로더는 클래스가 JVM 내에서 한 번만 로드되도록 보장하여 유일성을 유지
- 위임 모델에 의해 강제됨 (부모가 이미 클래스를 로드했다면 자식은 다시 로드하려고 시도하지 않음)
- **클래스는 FQCN과 해당 클래스를 로드한 클래스 로더**에 의해 고유하게 식별**

---

## Class Loader 계층 구조
- Java 는 세가지 주요 내장 Class Loader를 제공한다.
- 계층적 구조를 이루며, Bootstrap Class Loader를 제외한 모든 ClassLoader는 부모 ClassLoader를 가진다.

### [1] Bootstrap Class Loader
- 가장 높은 우선순위를 가지며, JVM 시작시 가장먼저 실행된다.
- Native 코드(C/C++) 로 구현되어 있으며, Java API의 핵심 클래스들을 로드한다.
- Load 하는 Class
  - `java.lang.object`, `Class`, `ClassLoader`, ...
- ClassPath
  - java8 이하: `jre/lib/rt.jar`
  - java9 이상: JavaRuntimeImage 내의 `/lib`


### [2] Extension Class Loader (= Platform Class Loader ( >= Java 9))
- Bootstrap Class Loader의 자식 ClassLoader로, Java 확장 라이브러리를 로드한다.
- 핵심 Java 확장 및 Platform 특정 Class Load


### [3] Application Class Loader (= System Class Loader)
- Extension Class Loader의 자식 ClassLoader로, 애플리케이션 클래스 경로에 있는 클래스를 로드한다.
- 주로 개발자가 작성한 애플리케이션의 클래스와 라이브러리를 로드하는 역할을 한다.


| 클래스 로더 이름 | 주요 책임 | 클래스패스/위치 (Java 8 vs. Java 9+) | 구현 언어 |
| --- | --- | --- | --- |
| **부트스트랩 클래스 로더** | JVM 핵심 API 및 필수 내부 클래스 로드 | Java 8: jre/lib/rt.jar<br>Java 9+: Java Runtime Image (JRT) 내 /lib | 네이티브 (C/C++) |
| **확장 클래스 로더** (Java 9+ Platform CL) | 핵심 자바 확장 및 플랫폼 특정 클래스 로드 | Java 8: jre/lib/ext 또는 java.ext.dirs<br>Java 9+: JDK 모듈 시스템 또는 --module-path | Java (Java 9+ `BuiltinClassLoader`) |
| **애플리케이션 클래스 로더** (System CL) | 사용자 정의 및 애플리케이션 레벨 클래스 로드 | `CLASSPATH` 환경 변수, `-classpath`, `-cp` 옵션 | Java |

