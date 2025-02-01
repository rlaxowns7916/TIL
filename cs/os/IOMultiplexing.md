# I/O Multiplexing
- 하나 혹은 적은 수의 Thread가 다수의 I/O(파일, 소켓, ...)의 Event를 감지하고 I/O를 수행 할 수 있게 한다.
- 각 I/O 마다 Thread를 할당 하는 것이 아니기 떄문에, 높은 처리량과 동시성 및 성능 개선을 이룰 수 있다.

## 발생 배경
```text
[기존 모델의 한계 (블로킹 I/O + 스레드 1:1)]
고전적인 방식으로는 각 연결마다 하나의 스레드를 두어 Blocking I/O를 처리한다.
연결 수가 많아지면 스레드가 기하급수적으로 증가하며, Context Switch 비용, 동시성 병목 등이 발생한다.

[효율적인 자원 활용]
I/O Multiplexing을 사용하면, 하나의 Thread가 동시에 수백~수천 개의 소켓을 감시할 수 있다.
준비된 Socket에만 실제 I/O 연산을 수행하므로, CPU와 메모리를 효과적으로 절약한다.

[Non-blocking I/O]
I/O Multiplexing은 주로 NonBlocking I/O와 함께 사용된다.
준비되지 않은 Socket 에 대해서는 곧바로 반환해 대기 시간을 제거하고, 준비되었을 때만 I/O를 수행한다.
```


## 주요 방식
1. select()
- 오래된 표준 함수 
- Linear Scan (순차 탐색)
- **고정 크기의 FileDescriptor 집합**을 통해 Read/Write 가능 여부를 Monitoring 한다.
- 모든 FD를 매번 스캔해야 해서, 대규모 연결에 비효율적
  - FD_SET 같은 매크로를 사용하며, 감시할 FD가 많아지면 성능이 저하된다.


2. poll()
- select()의 한계를 개선 
- Linear Scan (순차 탐색)
- **동적 할당 가능한 pollfd 배열**로 FD와 Event를 관리한다. (event에 관계업싱 다 linear scan 해야한다.)
- select와 유사하게 모든 FD를 매번 스캔해야 해서, 대규모 연결에 비효율적

3. epoll (Linux)
- Linux 에서 대규모 소켓 처리를 위해 도입된 고성능 SystemCall
- Event 기반
- 한 번 등록된 소켓(FD)을 epoll 내부 커널 자료구조에 보관해두고, 이벤트 발생 시 빠르게 확인
  - 최초에 넣고 Monitoring 주체가 Loop를 돌면서 확인하는 구조가 아니다.
  - 해당 Socket의 Event를 감지해 READY 자료구조에 넣는 구조
- 이벤트 기반(EPOLLET), 레벨 기반(EPOLLLT) 등 다양한 모드를 지원.

4. kqueue (BSD, macOS)
- BSD 계열, macOS에서 사용되는 고성능 Event 통지 메커니즘
- epoll과 비슷한 방식으로 동작하며, 파일, 소켓, 타이머, 시그널 등 다양한 이벤트를 감시

5. IOCP (Windows)
- Windows에 서 비동기 I/O 처리를 최적화
- 커널이 Completion Port에 이벤트를 통지하고, 사용자 레벨에서 CompletionRoutine으로 처리.