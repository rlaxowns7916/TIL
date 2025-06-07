# ForkJoinPool

## 개요
- JDK 1.7부터 추가된 Fork/Join Framework의 핵심 구성요소
  - `java.util.concurrent` 패키지에 위치
- Work-Stealing 알고리즘 기반

## 지원하는 Task 타입
### ForkJoinTask (추상 클래스)
- **RecursiveTask<V>**: 결과값을 반환하는 작업 (compute() 메서드에서 V 타입 반환)
- **RecursiveAction**: 결과값을 반환하지 않는 작업 (compute() 메서드에서 void)
- **CountedCompleter<T>**: 완료 작업을 명시적으로 카운트하는 작업

### 일반 Task
- **Runnable/Callable**: Fork 지원 안함, 내부적으로 ForkJoinTask.AdaptedRunnable/AdaptedCallable로 래핑

## 주요 특징

### 1. 작업 분할 (Fork)
- 큰 작업을 작은 하위 작업으로 재귀적으로 분할
- `fork()` 메서드를 통해 비동기적으로 하위 작업 실행
- 각 작업은 독립적으로 실행 가능한 단위로 분할

### 2. 작업 병합 (Join)
- 분할된 하위 작업들의 결과를 병합하여 최종 결과 생성
- `join()` 메서드를 통해 하위 작업의 완료를 대기하고 결과 수집
- 재귀적으로 상위 작업으로 결과 전파

### 3. Work-Stealing 알고리즘
- 각 워커 스레드는 자신만의 작업 큐(Deque) 보유
- 유휴 스레드는 다른 스레드의 큐 끝에서 작업을 훔쳐옴
- 작업 부하를 동적으로 균형 조정하여 CPU 활용도 최대화
- LIFO(Last In First Out) 방식으로 자신의 큐에서 작업 처리
- FIFO(First In First Out) 방식으로 다른 스레드의 큐에서 작업 훔침

## 사용 시 고려사항

### 적합한 경우
- **CPU-Bounded 작업**: 계산 집약적인 작업
  - ThreadBlokcing이 되면, Work-Stealing 의 장점이 무효화됨
- **재귀적으로 분할 가능한 작업**: 병합 정렬, 퀵 정렬 등
- **대규모 데이터 병렬 처리**: 배열, 컬렉션의 대량 처리
- **독립적인 하위 작업**: 작업 간 의존성이 없는 경우

### 부적합한 경우
- **I/O Bounded 작업**: 파일, 네트워크 I/O가 주된 작업
- **작은 작업**: Fork/Join 오버헤드가 실제 작업보다 큰 경우
- **동기화가 많이 필요한 작업**: 스레드 간 경합이 발생하는 경우
- **블로킹 작업**: 다른 스레드의 작업을 훔칠 수 없게 되어 성능 저하

## 주의사항
- Fork/Join 작업 내에서 동기화된 블록이나 I/O 작업은 피해야 함
- 작업 크기가 너무 작으면 오버헤드로 인해 성능 저하 발생
- commonPool은 JVM 전체에서 공유되므로 장시간 실행되는 작업은 별도 풀 사용 권장
- 재귀 깊이가 너무 깊으면 스택 오버플로우 발생 가능