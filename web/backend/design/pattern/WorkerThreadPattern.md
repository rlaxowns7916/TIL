# Worker Thread Pattern (워크 스레드 패턴)

멀티스레드 환경에서 **작업(Task) 생산(Producer)** 과 **작업 처리(Consumer/Worker)** 를 분리하고,
**고정/가변 개수의 워커 스레드 풀**이 **작업 큐(Work Queue)** 를 통해 작업을 가져가 처리하는 패턴이다.

- 목적: 생성되는 작업량의 변동을 **큐로 흡수**하고, 처리 스레드 수를 **통제(리소스 한도)** 하여 안정적으로 처리
- 대표 구현: Java `ExecutorService`/`ThreadPoolExecutor`, 서버의 Request 처리 스레드풀, DB Connection Pool(유사 개념)

---

## 언제 유용한가

- **I/O-bound** 작업(네트워크/DB/파일)처럼 대기 시간이 길고, 동시에 많은 요청이 들어오는 경우
- 작업 생성 속도(요청 유입)와 처리 속도(워커 처리량)가 일시적으로 불일치하는 경우
- 스레드를 무한 생성하면 위험한 경우(메모리/컨텍스트 스위칭 폭증)

반대로,
- **CPU-bound** 작업을 코어 수보다 훨씬 많은 스레드로 돌리면 오히려 성능이 악화될 수 있다.
  - 보통 `threads ~= CPU 코어 수` 근처가 유리(상황/런타임/GC/컨텍스트 스위칭 비용에 따라 조정)

---

## 구성 요소

### 1) Work Queue (작업 큐)
- 작업을 저장하는 자료구조 (대개 FIFO)
- 워커가 작업을 가져갈 때까지 **버퍼** 역할
- 동시성 안전(스레드 세이프)해야 함

예시
- Java: `BlockingQueue<Runnable>`
  - `ArrayBlockingQueue`, `LinkedBlockingQueue`, `SynchronousQueue` 등

### 2) Worker Threads (워커 스레드)
- 큐에서 작업을 가져와 처리
- 워커 개수는 리소스(코어/메모리/DB 커넥션/외부 API RPS 한도)에 맞춰 제한

### 3) Task (작업)
- 실행 단위
- Java에서는 보통 `Runnable`/`Callable` 로 모델링

---

## 동작 흐름(ASCII 다이어그램)

```
           (submit)
[Producer] ---------> [ Work Queue ] <---------
                          |   ^               |
                          v   | (take)        | (take)
                       [Worker-1]             |
                          |                   |
                          v                   |
                       [Worker-2]             |
                          |                   |
                          v                   |
                       [Worker-N] ------------

- Producer: 작업을 큐에 넣음
- Worker: 큐에서 작업을 꺼내 처리
```

---

## 핵심 설계 포인트(실무)

### 1) 큐 용량(capacity)과 백프레셔(backpressure)
- 큐를 **무제한(unbounded)** 으로 두면, 처리량보다 유입이 클 때 메모리 폭발로 이어질 수 있음
- 큐를 **유한(bounded)** 으로 두고, 포화 시 정책을 정해야 한다.

포화 시 전략 예시
- **Block**: submit을 막아 상류(요청자)로 자연스럽게 속도 조절
- **Drop**: 일부 작업을 버림(로그/메트릭 필수)
- **Caller-runs**: 호출자 스레드가 직접 실행하여 속도 조절(자연스러운 backpressure)

Java `ThreadPoolExecutor` 에서는 `RejectedExecutionHandler` 로 정책을 표현한다.

### 2) 워커 수(threads) 결정
- I/O-bound: 외부 리소스 대기 시간이 크므로 코어 수보다 크게 잡는 경우가 많음
- CPU-bound: 코어 수 근처로 제한하는 것이 일반적으로 유리
- **외부 병목(특히 DB 커넥션 수)** 를 상한으로 삼는 것이 안전

### 3) 작업의 경계와 실행 시간
- 작업이 너무 크면 큐가 "큰 덩어리"를 쌓아 지연이 커짐
- 너무 잘게 쪼개면 오버헤드(큐잉/스케줄링/컨텍스트 스위칭)가 커짐
- 가능하면 작업당 실행 시간 분포(p50/p95/p99)를 측정하고 조정

### 4) 장애/타임아웃/취소
- I/O 작업은 반드시 타임아웃을 두고, 워커가 영원히 묶이지 않게 해야 함
- 필요하면 `Future.cancel(true)` 등 **취소 가능**한 형태로 설계

### 5) 관측 가능성(Observability)
최소한 아래 메트릭은 보는 편이 좋다.
- 큐 길이(queue size), 큐 대기 시간
- 처리량(throughput), 작업 실행 시간
- rejected count(거절/드랍), active thread 수

---

## Java 예시: ThreadPoolExecutor

```java
int core = 8;
int max = 16;
int queueCapacity = 1000;

BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueCapacity);

ThreadPoolExecutor pool = new ThreadPoolExecutor(
    core,
    max,
    60, TimeUnit.SECONDS,
    queue,
    new ThreadPoolExecutor.CallerRunsPolicy() // backpressure
);

pool.submit(() -> {
    // 실제 작업 (반드시 타임아웃/예외 처리 고려)
});
```

- `CallerRunsPolicy()`는 큐가 꽉 차면 **submit한 스레드가 직접 실행**하여 유입 속도를 낮추는 효과가 있다.
- 처리량이 안정화될 때까지 모니터링하면서 `core/max/queueCapacity`를 조정한다.

---

## Connection Pool과의 관계(비유)

- Worker Thread Pattern: "작업"을 워커 스레드가 처리
- Connection Pool: "DB 커넥션"이라는 희소 리소스를 풀로 관리

둘 다 공통적으로
- 리소스 총량을 제한하여 안정성을 확보
- 대기열(혹은 대기)로 burst를 흡수

다만 Connection Pool은 **리소스 대여/반납**(acquire/release)이 핵심이고,
Worker Thread Pattern은 **작업 실행**이 핵심이다.

---

## 참고
- Java `ThreadPoolExecutor` Javadoc
- Brian Goetz, *Java Concurrency in Practice*
