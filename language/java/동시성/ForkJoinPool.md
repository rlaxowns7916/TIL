# ForkJoinPool
- jdk1.7 부터 추가
- java.util.concurrent 패키지에 있는 ThreadPool
- 작은 작업에대한 ForkJoin은 성능을 저하할 수 있다. (Fork ~ Join이 리소스를 꽤 잡아먹기 때문)
  - CPU-Bounded 작업에 적합
  - 병렬계산에 적합
  - 대규모 데이터 처리에 적합

## 지원하는 Task
- Runnable(Callable): Fork(작업분할) 지원안함 (내부적으로 ForkJoinTask로 Wrapping되서 사용한다.)
- RecursiveTask: 작업을 분할 할 수 있으며, subTask들이 결과 값을 반환한다.
- RecursiveAction: 작업을 분할 할 수 있으며, subTask들이 결과 값을 반환하지 않는다.

## 특징
### [1] 작업분할 (Fork)
- 작업을 작은 단위로 나누어서 여러 Thread에 병렬로 처리한다.
- 작업(ForkJoinTask)는 RecursiveTask(결과 O), RecursiveAction(결과 X)로 세분화된다.

### [2] 작업병합 (Join)
- 분할된 작업이 완료되면, 결과를 병합하여 최종결과를 생성한다.

### [3] Work-Stealing 알고리즘
- Thread가 유휴 상태일 때, 다른 Thread의 WorkQueue에서 작업을 훔쳐서 실행한다.
- 부하를 균등하게 분배하고, 병렬처리의 효율성을 높이는 효과가 있다.

### [4] Thread 관리
- JVM에서 사용할 수 있는 CPU 코어 수를 기준으로 Thread 수를 결정한다.
  - default는 availableProcessors() - 1

## 사용처
- java.util.stream
  - parallelStream()을 호출하면 ForkJoinPool.commonPool()을 통해 병렬로 처리한다.
- CompletableFuture
  - 내부적으로 ForkJoinPool.commonPool()을 사용한다.