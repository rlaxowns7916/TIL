# BlockingQueue
- java.util.concurrent 패키지에 존재한다.
- Queue가 특정 작업을 만족 할 때 까지 Thread를 차단한다.
  - ArrayBlockingQueue: 배열기반, 크기 고정
  - LinkedBlockingQueue: 링크드리스트 기반, 크기 무한정
- Producer-Consumer 패턴에서 사용된다.
  - Queue가 가득 차 있다면, 데이터 추가를 수행하는 Thread는 차단된다.
    - put()
    - offer()
    - offer(timeout)
    - add()
  - Queue가 비어있다면, 데이터를 획득하는 Thread는 차단된다.
    - take()
    - poll(timeout)
    - remove()
- **내부적으로 ReentrantLock + Condition을 사용한다.**


| 메소드      | 큐가 꽉 찼을 때                   | 큐가 비었을 때                    | 반환/예외 유형                    |
|-------------|-----------------------------------|-----------------------------|-----------------------------------|
| **add(E)**  | `IllegalStateException` 발생       | -                           | 성공 시 `true`, 실패 시 예외       |
| **offer(E)**| `false` 반환                      | -                           | 성공 시 `true`, 실패 시 `false`   |
| **put(E)**  | 큐에 공간이 생길 때까지 블록       | -                           | 반환 없음 (void)                  |
| **remove()**| -                                 | `NoSuchElementException` 발생 | 제거된 요소 반환                  |
| **poll()**  | -                                 | `null` 반환                   | 제거된 요소 반환 혹은 `null`      |
| **take()**  | -                                 | 요소가 생길 때까지 블록               | 제거된 요소 반환                  |
| **peek()**  | -                                 | `null` 반환  (제거는 아님)         | 요소 반환 혹은 `null`             |