# 동기(Synchronous) vs 비동기(Asynchronous)
- Request에 따른 Response를 기다리느냐의 차이이다.

## 동기(Synchronous)
- 코드의 순차적인 실행 및 실행결과를 대기하는 것이다.
  - 보통 Request에 해당하는 Response를 받기전까지 대기한다.
- 코드를 파악하기 쉽고, 유지보수 및 디버깅에 유리하다.


## 비동기(Non-Synchronous)
- 병렬적으로 수행하는 방식이다.
- Request에 따른 Response를 대기하지 않는다.
  - 보통 Response를 받았을 때 실행할 Callback 함수를 정의한다.
- 작업의 종료 순서가 일정하지 않다.

# 블로킹(Blocking) vs 논 블로킹 (Non-Blokcing)
- 작업의 제어권 소유 여부이다.

## 블로킹 (Blocking)
- 호출하는 쪽에 제어권을 넘긴다.
- 호출하는쪽의 작업이 종료되면 제어권을 넘겨받는다.
- Blocking + Synchronous
  1. 함수를 호출하면서 제어권을 넘긴다. 
  2. 호출한 함수가 종료되면 제어권과 응답을 넘겨 받는다. 

## 논 블로킹 (Non-Blokcing)
- 호출하는 쪽에 제어권을 넘기지 않는다.
- Async + NonBlokcing
  1. 함수를 호출하면서 Callback 함수를 같이넘겨준다.
  2. Response를 대기하지 않고 다음 로직을 실행한다.
  3. 호출한 함수가 종료되면 Callback함수가 실행된다.
- Sync + Non-Blocking
  1. 함수를 호출하면서 제어권은 넘기지 않는다.
  2. 다음 로직을 실행하기 전까지 BusyWait로 호출한 함수의 종료여부를 묻는다.