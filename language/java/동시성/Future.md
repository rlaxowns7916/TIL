 # Future
 - java5에 도입
 - Java에서 비동기 프로그래밍에 사용되는 패턴이다.
   - 작업의 결과를 처리하거나, 추가동작을 정의하며 사용한다.

## [1] Future
- 실제 구현체는 **FutureTask**이다.
   - FutureTask는 Runnable과 Future를 구현한 클래스이다.
- 비동기 작업의 결과를 가지고 있다. 
  - 완료되지 않은 상태로 get()을 호출하면, 호출자는 Blocking된다.
- 취소가 가능하다.
 
### 명령어

1. get()
   - 해당 타입의 결과를 반환한다.
   - Future의 작업이 완료되지 않았다면, Blocking된다.
   - **작업이 Cancel이 되어있는 경우에 호출하면 CancellationException이 발생한다.**
   - **작업이 연산 도중 Exception이 발생한 경우에 호출하면 ExecutionException이 발생한다.**
   - **timeout인자를 제공한 연산의 경우, 지정된 시간이 지나면 TimeoutException이 발생한다.**
2. cancel(mayInterruptIfRunning)
   - 작업을 취소한다.
     - 취소가 성공하면 true, 실패하면 false를 반환한다.
   - mayInterruptIfRunning: 작업이 실행중일 때, Interrupt를 보낼지 여부
     - true: 작업이 실행중이라면, Interrupt를 보내서 작업을 중단한다.
     - false: 작업이 시작되지 않았다면, 취소한다.


## ExecutorSevice에서 Future 반환과정
1. ExecutorService의 Future객체 생성
2. ExecutorService는 FutureTask를 작업 큐에 제출하고, ThreadPool이 작업 실행 
3. Caller에게 Future를 리턴
4. Caller는 Future.get()으로 결과값을 받아온다.
   - 내부적으로 Caller Thread를 Blocking된다.
5. Task완료 시, Future에 값 set 
   - 내부적으로 Caller Thread 를 깨운다.
