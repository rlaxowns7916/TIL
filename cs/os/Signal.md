# Signal
- 리눅스 시스템 상에서 외부에 발생한 비동기적인 이벤트를 프로세스에게 알려주는 일종의 소프트웨어적인 인터럽트 역할
  - Process에서 Signal에 대한 처리가 가능하다.
    - SIGKILL, SIGSTOP은 처리가 불가능하며 무조건 적용된다.
- 아래와 같은 목적이 있다.
  - Process 종료 및 제어
  - 예외상황 처리
  - 사용자 정의 Signal
  - 외부 이벤트 처리

| 시그널 이름 | 번호 | 설명 |
|------------|------|------------------------------------------------|
| SIGHUP     | 1    | 터미널 연결이 끊겼을 때 발생 |
| SIGINT     | 2    | 인터럽트(Ctrl+C) 발생 |
| SIGQUIT    | 3    | 강제 종료(Ctrl+\) |
| SIGILL     | 4    | 잘못된 명령어 실행 (Illegal instruction) |
| SIGABRT    | 6    | `abort()` 함수 호출 시 발생 |
| SIGFPE     | 8    | 부동소수점 예외 (0으로 나누기 등) |
| SIGKILL    | 9    | 강제 종료 (프로세스가 무조건 종료됨) |
| SIGSEGV    | 11   | 잘못된 메모리 접근 (Segmentation Fault) |
| SIGPIPE    | 13   | 파이프가 깨졌을 때 (Broken pipe) |
| SIGALRM    | 14   | `alarm()`에 의해 타이머 만료 시 발생 |
| SIGTERM    | 15   | 정상 종료 요청 (`kill` 명령어 기본 값) |
| SIGCHLD    | 17   | 자식 프로세스가 종료되었을 때 부모에게 전달됨 |
| SIGCONT    | 18   | 정지된 프로세스 재개 (`fg`, `bg` 명령어) |
| SIGSTOP    | 19   | 프로세스 정지 (`kill -STOP`) |
| SIGTSTP    | 20   | 터미널에서 `Ctrl+Z` 입력 시 발생 |
| SIGUSR1    | 30   | 사용자 정의 시그널 1 |
| SIGUSR2    | 31   | 사용자 정의 시그널 2 |
