# Runnable vs Callable
- 두가지 모두, 별도의 Thread에서 실행할 수 있는 작업을 나타내는 인터페이스이다.

# [1] Runnable
- run()의 반환타입은 void이다.
- Exception이 선언되어있지 않기 떄문에, CheckedException을 던질 수 없다.

# [2] Callable
- java.util.concurrent에서 제공된다.
- call()의 반환타입은 Future<V> (Generic) 이다.
- Exception을 던지기 때문에, 그 하위 예외인 CheckedException을 모두 던질 수 있다.

# 요약
| Runnable                             | Callable                                                          |
|--------------------------------------|-------------------------------------------------------------------|
| void run()                           | V call() throws Exception                                         |
| 리턴 값이 없고, 예외도 던지지 않는다.               | 리턴 값이 존재하며, 예외 또한 존재한다.                                           |
| 리턴 값이 없기 때문에, 작업이 완료되었는지 여부를 알 수 없다. | 리턴 값이 있기 때문에, 작업이 완료되면 결과를 추적 가능하다. (ExecutorService -> submit()) |