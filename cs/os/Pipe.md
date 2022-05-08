# Pipe (파이프)
- 단방향 흐름이다.
- IPC의 한 종류 이다.
  - 부모<->자식 관계에서만 사용 가능하다.
- 한 프로세스에서 다른 프로세스르 데이터를 넘겨 줄 수 있다.
- 앞 프로세스의 Output이 뒤 프로세스의 Input이 된다.
- 프로세스 명령어들의 Chaining이 가능하다.
- '|' 키워드를 통해서 사용한다.

## Pipe의 구현
- 커널영역에 생성된 BufferedStream 이다.
  - 2개의 FileDiscriptor로 연결된다.
  - 한개는 Read, 한개는 Write용 이다.
- 부모 프로세스가 자식프로세스를 생성한다.
  - 부모는 Write, 자식은 Read가 가능하다.

```shell
$ ps -aux | grep java  # Java와 관련된 프로세스 찾기
```