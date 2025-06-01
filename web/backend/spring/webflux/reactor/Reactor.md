## Reactor란 무엇인가
- Spring WebFlux의 핵심 구성 요소로, **Reactive Streams** 표준을 구현한 Java 라이브러리
- JVM 환경에서 비동기적이고 논블로킹 방식의 프로그래밍을 가능하게 하며, **백프레셔(backpressure)** 처리 기능을 내장하고 있다.
- 함수형 프로그래밍 패러다임을 채택하여 데이터 스트림을 선언적으로 처리할 수 있게 해주며, 높은 처리량과 낮은 메모리 사용량을 제공한다.

## Marble Diagram
- Marble Diagram은 **시간에 따른 데이터 스트림의 흐름을 시각적으로 표현**하는 다이어그램. 
- 수평선 위에 원(marble)으로 이벤트를 표시하고, 시간의 흐름은 왼쪽에서 오른쪽으로 나타낸다. 
- Spring 공식 문서와 Reactor 문서에서 각 연산자의 동작을 설명할 때 광범위하게 사용된다.

## Publisher
- 두 타입 모두 **지연 실행(lazy execution)** 특성 
- `subscribe()`가 호출되기 전까지는 실제 작업이 수행되지 않는다.

### [1] Mono
- **0개 또는 1개의 요소를 비동기적으로 방출하는 Publisher**
- 성공적으로 값을 방출하거나(`onNext` + `onComplete`), 값 없이 완료되거나(`onComplete`), 에러로 종료될 수 있습니다(`onError`).
- 다양한 연산자가 있다.
  - concatWith: Mono<T> + Mono<T> = Flux<T>
    - 결합 순서가 보장된다.
  - zipWith: Mono<T> + Mono<U> = Mono<Tuple2<T, U>>
  - mergeWith: Mono<T> + Mono<T> = Flux<T>
        - 완료된 순서대로 emit된다.
  - and: Mono<T> + Mono<U> = Mono<Void>
    - 두 Mono가 모두 완료되면 complete signal을 방출한다.

## [2] Flux
- **0개부터 N개의 요소를 비동시적으로 방출하는 Publisher** 
- 연속적인 데이터 스트림이나 컬렉션 처리에 적합 
- Flux는 여러 개의 `onNext` 시그널을 방출한 후 `onComplete` 또는 `onError`로 종료 
- 다양한 연산자가 존재한다.
  - map: 각 요소를 다른 형태로 변환 Flux<T> → Flux<U>
  - filter: 조건에 맞는 요소만 통과 Flux<T> → Flux<T>
  - flatMap: 각 요소를 Publisher로 flatten 한 후 map Flux<Flux<T>> → Flux<U>
  - concatMap: flatMap과 유사하지만 순서 보장 Flux<T> → Flux<U>
  - reduce: 모든 요소를 하나의 값으로 축약 Flux<T> → Mono<T>
  - collectList: 모든 요소를 List로 수집 Flux<T> → Mono<List<T>>
  - take: 처음 n개 요소만 취함 Flux<T> → Flux<T>
  - skip: 처음 n개 요소를 건너뜀 Flux<T> → Flux<T>
  - distinct: 중복 제거 Flux<T> → Flux<T>
  - switchMap: 새로운 Publisher가 오면 이전 것을 취소 Flux<T> → Flux<U>

## Sequence
- **Flux와 Mono를 포함하는 상위 개념**

### ColdSequence
- **정의**: 구독(subscribe) 시점에 데이터 소스가 새로 생성되며, 각 구독자마다 독립적으로 전체 시퀀스를 재생(start)한다.
- **특징**
  - **지연 실행(Lazy Execution)**
    - 구독이 발생하기 전까지 실제 데이터 생성이 이루어지지 않는다.
    - `subscribe()` 호출 시마다 소스가 새롭게 평가되어 데이터 흐름이 시작된다.
  - **독립적 구독(Independent Subscription)**
    - 여러 구독자가 존재하더라도, 각 구독자는 처음부터 끝까지 동일한 데이터 스트림을 별도로 수신한다.
    - 예: `Flux.just(1, 2, 3)`, `Flux.fromIterable(...)`, `Mono.fromCallable(...)` 등
  - **백프레셔(Backpressure) 보장**
    - 구독자가 요청한 만큼(onNext 요청량) 데이터를 순차적으로 방출한다.
    - 수요가 없으면(onRequest 미존재) 데이터 생성 또는 방출을 일시 중단한다.
  - **Marble Diagram**
    - 각 구독자마다 동일한 형태의 마블이 좌측(시작)부터 우측(완료)까지 재생산된다.

#### ColdSequence 예시
- `Flux.range(1, 5)`
  - 구독 시마다 1, 2, 3, 4, 5를 순서대로 방출하고 완료(onComplete)
- `Mono.fromCallable(() -> computeValue())`
  - 구독 시마다 `computeValue()`가 호출되어 결과를 onNext → onComplete

---

### HotSequence
- **정의**: 데이터 소스가 구독 여부와 무관하게 이미 생성 혹은 방출을 시작하며, 구독 시점 이후 방출되는 데이터만 수신한다.
- **특징**
  - **즉시 실행(Eager Execution)**
    - 소스가 생성되면 별도 구독 없이 데이터 방출이 시작될 수 있으며, 구독이 늦어지면 중간 데이터가 손실될 수 있다.
    - 예: 외부 이벤트 스트림, 웹소켓 메시지, 시스템 타이머, 센서 등
  - **공유 및 멀티캐스팅(Shared / Multicasting)**
    - 하나의 데이터 스트림을 여러 구독자가 공유(shared)하여 소비한다.
    - 대표적인 구현체:
      - `ConnectableFlux` (예: `Flux.publish()`, `Flux.share()`)
      - `Sinks.Many` (예: `Sinks.many().multicast()`, `Sinks.many().replay()`)
      - `Processor` 계열 (예: `DirectProcessor`, `EmitterProcessor` 등)
  - **백프레셔 제한**
    - 모든 구독자가 공통으로 소비하기 때문에, 특정 구독자의 요청량이 부족해도 소스는 계속 방출할 수 있다.
    - 버퍼링 전략 혹은 드롭(drop) 전략이 필요할 수 있다.
  - **중간 합류(Late-Joiner) 문제**
    - 구독 시점이 뒤처질수록 이미 방출된 데이터는 수신할 수 없으며, 가장 최신(onSubscribe 이후)의 데이터만 수신한다.
    - 예외적으로 `replay()` 기반의 HotSequence는 지정한 개수만큼 과거 데이터를 버퍼링하여 신규 구독자에게 재전송할 수 있다.

#### HotSequence 예시
- `Flux.interval(Duration.ofMillis(500)).share()`
  - 프로그램 실행 시점부터 0.5초마다 Long 값을 방출하기 시작
  - 구독 시점 이후부터의 값만 onNext로 수신하며, 과거 데이터는 수신 불가
- `Flux.cache()`
  - 구독 시점부터의 데이터 스트림을 캐싱하여, 이후 구독자에게 동일한 데이터를 방출
  - 캐시된 데이터는 구독자가 요청할 때마다 재사용 가능
- `Sinks.many().multicast().onBackpressureBuffer()`
  - 내부적으로 구독자가 요청한 만큼만 방출하면서, 과도한 데이터는 버퍼링
  - 구독자가 없더라도 `tryEmitNext()` 호출 시 데이터가 내부 버퍼로 쌓여 대기
- `ConnectableFlux<String> hot = Flux.<String>create(emitter -> { ... }).publish(); hot.connect();`
  - `connect()` 호출 후 외부 이벤트 발생 시 즉시 방출 시작
  - 구독자는 `hot.subscribe(...)` 시점부터 이벤트를 수신하며, 구독 전 방출된 이벤트는 수신 불가

---

#### ColdSequence vs HotSequence 비교 요약
| 구분                  | ColdSequence                                     | HotSequence                                          |
|---------------------|-------------------------------------------------|-----------------------------------------------------|
| 실행 시점            | 구독 시점에 실행 시작                              | 소스 자체가 이미 구동되거나, connect 호출 시 실행 시작      |
| 구독자 간 상호 영향      | 독립적: 각 구독자는 처음부터 동일한 데이터를 순차적으로 수신         | 공유: 구독자마다 수신 시점이 달라 데이터 누락 가능            |
| 백프레셔 동작          | 온전한 백프레셔 지원: 구독자 요청량 기준으로 방출                | 부분적/제한적 백프레셔: 공유 버퍼 전략이나 드롭 전략 필요         |
| 사용 사례            | 데이터베이스 조회 결과, 파일 읽기, 정적인 리스트 스트림 등         | 외부 이벤트, 센서 데이터, 메시지 브로커(예: Kafka→Flux) 등      |
| 멀티캐스트 / 프로세싱   | 구독자별 독립 처리                                   | 하나의 소스를 공유하며 방송(멀티캐스트)                      |
| 재실행 가능성          | 구독마다 다시 평가(Re-play)                            | 기본적으로 재실행 불가(Late-Join 시 과거 데이터 손실)          |

---
## BackPressure
- **데이터 생산자(Publisher)가 소비자(Subscriber)의 처리 능력을 초과하지 않도록 제어하는 메커니즘**
- 소비 속도가 느린 Subscriber가 과도한 onNext 이벤트를 처리하다가 오버플로우(메모리 부족, OOM 등)를 겪지 않도록 보장


### Reactive Streams 표준과 BackPressure 흐름
1. **Publisher, Subscriber, Subscription**
  - **Publisher**: 데이터를 생성(onNext)하여 공급
  - **Subscriber**: 데이터를 소비하고, 처리 가능량을 Subscription에 “요청(request)”
  - **Subscription**: Subscriber와 Publisher 사이의 인터페이스 역할을 수행하며, `request(n)`과 `cancel()` 메서드를 통해 BackPressure를 제어
2. **데이터 요청(Request) 기반**
  - Subscriber가 `onSubscribe(Subscription s)`를 통해 Subscription을 받으면, 첫 번째로 `s.request(n)`을 호출하여 처리 가능한 요소의 개수(n)를 지정
  - Publisher는 Subscriber가 요청한 수량(n)만큼 `onNext(value)`를 호출하여 데이터를 전송
  - Subscriber가 다시 데이터를 더 받을 준비가 되면 추가로 `request(m)`을 호출하여 연속적인 데이터 흐름을 유지
3. **onComplete / onError**
  - Publisher가 더 이상 방출할 데이터가 없으면 `onComplete()`을 호출하고 스트림을 종료
  - 예기치 않은 에러가 발생하면 `onError(Throwable)`로 구독자에게 알리고 스트림을 종료


### BackPressure 전략
| 종류     | 설명                                                                                           |
|--------|----------------------------------------------------------------------------------------------|
| IGNORE | BackPressure 적용(X)                                                                           |
| ERROR  | DownStream으로 전달 할 때, Buffer가 가득 찰 경우 Exception                                               |
| DROP   | DownStream으로 전달 할 때, Buffer가 가득 찬 경우 Buffer 밖에서 가장 먼저 Emit된 데이터 DROP                         | 
| LATEST | DownStream으로 전달 할 때, Buffer가 가득 찬 경우 Buffer 밖에서 가장 최근에 Emit된 데이터 DROP                        |
| BUFFER | DownStream으로 전달 할 때, Buffer가 가득 찬 경우 Buffer 안에 있는 데이터 DROP   \n DROP_LATEST와 DROP_OLDEST가 존재 |