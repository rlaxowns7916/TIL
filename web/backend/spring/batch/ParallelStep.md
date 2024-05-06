# ParallelStep
- SplitState를 사용하여, 여러개의 Flow들을 병렬적으로 실행하는 구조
- 실행이 다 완료된 후, FlowExecutionStatus 결과들을 취합하여, 다음단계를 결정한다.


# SplitState
```java
// 병렬로 수행할 Flow들을 담은 컬렉션
Collection<Flow> flows;

// Thread를 생성하고, Task를 할당
TaskExecutor taskExecutor;

// 병렬로 수행 후, 하나의 종료 상태로 집계하는 클래스
FlowExecutionAggregator flowExecutionAggregator;

// Task를 내부적으로 Future 형태로 만들어서, TaskExecutor가 실행하게 한다.
FlowExecutionStatus handle(final FlowExecutor executor);
```

# 과정
```text
                 Job
                  |
                  |
                  |
                  V
               SimpleFlow
                  |
                  |
                  |
                  V
               SplitState
                  |
                  |
                  |
                  V
             TaskExecutor
    |              |              |
    |              |              |
    |              |              |
    V              V              V   
   worker1       worker2        worker3

FutureTask     FutureTask    FutureTask 
(SimpleFlow)  (SimpleFlow)  (SimpleFlow)
    |               |             |
    |               |             |
    |               |             |
    V               V             V
FlowExecution   FlowExecution   FlowExecution
    |               |             |
    |               |             |
    |               |             |
    V               V             V                  
           Collection<FlowExecution>
```
- Job은 SimpleFlow를 갖는다.
- SimpleFlow하위에는 SplitState를 가지고 있고, SplitState는 여러개의 Flow를 갖는다.
- SplitState는 TaskExecutor를 가지고있고, Thread를 생성한다.
- TaskExecutor는 Flow를 FutureTask로 만들어서, TaskExecutor가 실행하게 한다.
- FutureTask는 내부에 한개의 SimpleFlow를 가지고 있다.
- FutureTask의 결과는 FlowExecution에 모이고, Collection에 저장된다.

# 형태
```java
public Job job(){
    return JobBuilder(SAMPLE_JOB,jobRepository)
            .start(flow1())
            .split(TaskExecutor).add(flow2(),flow3())
            .next(flow4())
            .end()
            .build();
}
```
- Flow1을 생성한다.
- Flow2와 Flow3을 생성하고, 총 3개의 Flow를 합친다.
  - TaskExecutor에서 Flow 개수 만큼 Thread를 생성해서 각 Flow를 실행시킨다.
- Flow4는 Split처리가 완료 된 후 실행된다.