# Coroutine State
- Coroutine의 LifeCycle이다.
- Coroutine Job은 toString()을 구현했기 때문에, 출력으로 상태를 쉽게 알 수 있다.

# [1] New
- Active 이전, 방금 생성된 Coroutine을 나타낸 상태
  - Lazy Coroutine을 생성하면 쉽게 확인 할 수 있다.

# [2] Active
- Coroutine이 실행중인 상태 (isActive가 true인 상태)

# [3] Completing
- 실행 완료 대기 중 상태
  - 부모 Coroutine은 모두 실행이 완료되었으나, 자식 Coroutine의 완료를 기다리는 상태 

# [4] Completed
- 실행이 모두 완료된 Coroutine을 나타낸 상태
    - launch를 통해 Job을 실행하고, join()을 통해서 해당 Job의 완료 후 상태를 확인 할 수 있다.
- isCompleted는 true를 리턴한다.

# [5] Cancelling
- cancel() 호출을 통해서, 취소 Flag가 세워진 Coroutine을 나타낸 상태 
  - suspend point가 없는 Job을 취소하면 확인 가능하다.
- isCanceled()도 true를 리턴한다.
- isActive()는 false를 리턴한다.

# [6] Cancelled
- cancel()이 완료된 Coroutine을 나타낸 상태
  - cancelAndJoin() 호출 후, 해당 Job의 상태로 확인 가능하다.
- isCanceled()도 true를 리턴한다.
- isCompleted()도 true를 리턴한다.