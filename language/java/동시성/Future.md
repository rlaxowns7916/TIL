 # Future
 - Java에서 비동기 프로그래밍에 사용되는 패턴이다.
   - 작업의 결과를 처리하거나, 추가동작을 정의하며 사용한다.
 
## vs Callback
| 구분     | Future                    | Callback                                        |
|:-------|:--------------------------|:------------------------------------------------|
| 정의     | 비동기 작업의 결과 나타내는 객체        | 비동기 작업이 완료되었을 때, 수행할 동작을 정의한 Interface 또는 Class |
| 블로킹 여부 | 비동기 작업이 완료될 때 까지 Blocking | 비동기작업이 완료되면 Callback을 호출 -> NonBlocking         |
| 작업 결과  | 비동기 작업이 완료되면 받을 수 있다.     | Callback Method를 통해서 작업결과를 처리한다.                
| 용도     | 비동기작업의 결과를 받아옴            | 비동기 작업 완료 후 춯가 동작을 정의                           |



## ExecutorSevice에서 Future 반환과정
1. ExecutorService의 Future객체 생성
2. ExecutorService는 FutureTask를 작업 큐에 제출하고, ThreadPool이 작업 실행 
3. Caller에게 Future를 리턴
4. Caller는 Future.get()으로 결과값을 받아온다.
   - 내부적으로 Caller Thread를 Blocking된다.
5. Task완료 시, Future에 값 set 
   - 내부적으로 Caller Thread 를 깨운다.
