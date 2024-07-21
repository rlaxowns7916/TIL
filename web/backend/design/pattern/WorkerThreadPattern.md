# WorkerThreadPattern
- MultiThread 에서 여러개의 작업을 병렬적으로 처리할 때 사용하는 패턴
- CPU, I/O Bound Job에 유리하다.
- Connection Pool이 예시이다.

## [1] Work Queue
- Task를 저장하는 자료구조, FIFO 방식으로 동작한다.
- Producer Thread가 Task를 WorkQueue에 추가하고, WorkerThread가 Polling 하여 Task를 수행한다.
- 동기화 처리가 필수적이다.

## [2] Worker Thread
- Work Queue에서 Task를 Polling하고 작업을 처리하는 Thread이다.
- N개의 WorkThread가 작업르 가져와 병렬로 처리한다.

## [3] Task
- 작업을 실행하는 단위
- Java에서 Runnable이나 Callable 인터페이스를 구현하면된다.

