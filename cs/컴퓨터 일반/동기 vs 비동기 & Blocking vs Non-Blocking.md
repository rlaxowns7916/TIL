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


## I/O 에서의 동기 비동기
```

## 동기 ##
동기의 경우, Thread가 I/O의 작업이 끝날 때 까지 대기하게된다.
이 말은, 작업 처리시간의 상당부분을 대기시간이 잡아먹는 다는 의미이고,
자원을 효율적으로 사용하지 못한다는 이야기가 된다.

또한 ContextSwitching이 빈번하게 발생하기 때문에, 이 또한 오버헤드가 된다.
I/O 대기하는 Thread의 증가 -> Context Switching비용 증가 -> Thread가 잡고있는 메모리 떄문에, 메모리 사용의 증가

Server에서의, Request 당 Thread 모델은, 일정 시점이 넘어가게 되면 더 이상의 성능증가를 보이지 않는다.


## 비동기 ##
비동기의 경우, Threa가 I/O가 끝날 떄 까지 대기하지 않는다.
I/O작업이 진행되는 동안, 해당 Thread는 다른 작업을 수행하고
I/O가 완료되면, Call-back 형태의 결과를 받는다.

그렇기 때문에 고정된 숫자의 Thread만 사용하는 것이 가능하게 된다.
이러한 동작으로 인해서
1. 고정된 Thread로 인해 Memory의 사용량이 줄어든다.
2. Context-Switching의 오버헤드가 상대적으로 적다.
3. I/O를 대기하지 않기 떄문에 응답시간이 줄어든다.

와 같은 이점을 얻을 수 있게 된다.
하지만 비동기는 흐름을 파악하기 어렵고, 복잡도를 올린다는 문제점이 있다.
```