# EventLoop
- Netty의 Event처리를 담당하는 Thread 및 실행 루프의 개념
  - **Single Thread + WorkQueue**
  - Runnable이 Work의 단위이며, Queue에 넣고 실행하기를 반복한다.
- **EventLoop는 하나의 Thread를 가지며, 여러 Channel의 작업을 수행 가능하다.**
  - EventLoop(1) : Channel(N) // NIO
  - EvnetLoop(1) : Channel(1) // OIO
- Event들은 Queue에 쌓이며, EventLoop는 이 Queue를 순회하면서 Event를 처리한다.
  - **감지한 Event를 ChannelPipeLine 혹은 ChannelHandler에게 전달한다.**
  - SingleThread이기 떄문에, ChannelHandler에서 동기화에 대한 고민을 하지 않아도 된다.
- Event처리 외에도, schedule()을 통한 Task 스케줄링도 가능하다.
- EventLoop를 여러개 사용하면, 각각의 Thread가 병렬로 여러개의 Channel에 대한 처리가 가능해진다.

## 예시 코드
### [1] 이벤트 루프 처리
```java
while(!teminated){
    List<Runnable> readyEvents = blockUntilEventsReady();
    for(Runnable ev : readyEvents){
        ev.run();    
    }
}
```
- FIFO 방식이다.

### [2] EventLoop Task 수행 흐름
```text
Caller Thread          EventLoop (Thread + Queue)

    |                          |
    |-- execute(task) -------->|
    |                          |
    |-- inEventLoop() check -->|
    |                          |
 [Caller Thread == EventLoopThread]               
    |         YES              |
    |------------------------->| task.run() 직접 실행
    |                          |
    |         NO               |
    |------------------------->| taskQueue.add(task)
    |                          |
    |                          | -- EventLoop 루프 내에서 --
    |                          | while (true) {
    |                          |   task = taskQueue.poll()
    |                          |   task.run()
    |                          | }
```
- CallerThread와 EventLoop Thread는 다를 수 있다.
  - 다른 Executor에서 Channel 접근
  - FutureListener, Timer, Callback 등

### [2] 스케줄

## EventLoopGroup
- EventLoop를 관리하는 그룹
  - 보통 EventLoop 단일이 아닌, Group으로 관리한다.
- 새로운 Channel이 생성되면, EventLoopGroup은 EventLoop를 할당한다. (부하분산)
- 여러 구현체가 있다.
  - NioEventLoopGroup 
  - EpollEventLoopGroup, 
  - KQueueEventLoopGroup 
  - ...
- ServerBootStrap의 경우 2개가 필요하다.
  - BossGroup (Server Binding / Listen)
  - WorkerGroup (Socket I/O, Event 처리)
- BootStrap의 경우 1개가 필요하다.
  - Socket I/O, Event 처리


## Event 감지의 주체
```text
Netty는 Java Nio Selector를 사용하여 Event를 감지한다.
EventLoopGroup에 Channel을 등록하면, 그에따라 알맞은 EventLoop에게 할당이 되며,
해당 EventLoop는 내부적으로 Selector를 통해 Event를 감지하고 처리한다.
```

