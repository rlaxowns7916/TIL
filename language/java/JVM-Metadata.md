# Metadata
- JVM의 MethodArea를 구현하는 실제 구현체 (Java8+)
- JVM이 클래스 및 메서드의 구조, 필드, 타입 등의 정보를 관리하는 데 사용하는 데이터를 저장하는 영역
- Reflection 등을 통해 클래스나 메서드에 대한 정보를 동적으로 참조하고 조작할 수 있도록 지원하는 역할 
- Metadata를 통해서 JVM은 클래스 로딩, 동적 바인딩, 어노테이션 처리 등을 수행

## Method 영역아닌가?
- Method Area는 Spec에 정의된 추상적인 개념일 뿐이며, 실제 JVM 구현체마다 구현이 다를 수 있다.
  - HotspotJVM 기준
    - Java7-: PermGen (Heap)
      - `OOM: PermGen Space 를 자주 발생시킴`
      - 개발자가 고정된 크기를 지정해줘야함
    - Java8+: Metaspace (Native memory)
      - `Native memory stack에 위치하고, 가변이기에 개발자 편의성 향상`
      - 시스템 전체의 Native 메모리를 고갈시켜 OOM Killer의 대상이 될 수 있다

## vs PermGen
| 항목                    | PermGen (Java 7 이하)                      | Metaspace (Java 8 이상)                           |
|-------------------------|--------------------------------------------|-------------------------------------------------|
| 메모리 위치             | JVM 힙(Heap) 영역 내부                      | 네이티브 메모리 영역 (JVM 외부)                            |
| 크기 관리               | 고정 크기 (설정값 초과 시 OOM 발생)         | 자동 크기 확장 (필요 시 동적 증가)                           |
| 메모리 누수 시 영향     | PermGen 영역 고갈 (OOM: PermGen space 발생) | 전체 시스템 메모리 소진 위험 (Native memory stack에 위치하기 떄문) |
| 클래스 로딩 환경 최적화 | 동적 로딩 환경에 취약 (쉽게 고갈됨)         | 동적 로딩 환경에 최적화됨 (성능 및 안정성 향상)                    |
| GC 성능                 | 상대적으로 낮음                             | 개선된 성능 (효율적 메모리 관리)                             |

## Metaspace 내부 아키텍처 
- Native Memory (off-heap) --> OS Memory를 사용한다는 것 
- Arena-Based Allocator: 클래스 로더별 독립 메모리 영역 
- Buddy Allocator: 청크 단위 메모리 관리, 단편화 최소화 
- Elastic 관리: 커밋 그래뉼 단위 메모리 할당 및 해제

## 저장되는 데이터
- 클래스 메타데이터: 클래스 구조, 메서드 바이트코드, vtable
- 런타임 상수 풀: 심볼릭 참조의 런타임 해석
- 정적 변수: 변수 자체는 Metaspace, 객체는 힙 저장
- 어노테이션 및 JIT 데이터: 런타임 정보 및 최적화 데이터
- Compressed Class Space: 64비트 환경에서 포인터 압축용 특수 공간(기본 1GB 고정)

## Metaspace의 동적 동작 및 GC
- Metaspace GC는 오직 Full GC 시에만 발생
- High-Water Mark(HWM) 초과 시 GC 유발 및 HWM 조정
  - JVM 설정을 통해서 조정 가능
- 초기 잦은 GC는 정상적이며 적응 과정임

## Metaspace JVM 파라미터
| 파라미터                        | 목적                                      | 기본값     |
|-------------------------------|-------------------------------------------|------------|
| `-XX:MetaspaceSize`           | 초기 GC 임계값(HWM 설정)                  | 약 21MB     |
| `-XX:MaxMetaspaceSize`        | Metaspace 최대 크기 제한 (OOM 방지)       | 무제한      |
| `-XX:CompressedClassSpaceSize`| 압축 클래스 공간 크기                      | 1GB         |
| `-XX:MinMetaspaceFreeRatio`   | GC 후 최소 여유 공간 유지 비율           | 40%         |
| `-XX:MaxMetaspaceFreeRatio`   | GC 후 최대 여유 공간 유지 비율           | 70%         |
