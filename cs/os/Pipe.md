# Pipe (파이프)

## Pipe 종류
### [1] Anonymous Pipe
- **단방향 데이터 스트림이며, FIFO 방식이다.**
- **파일 시스템에 저장되지는 않는다.(임시적으로 생성)**
- 부모자식 관계, Shell에서의 명령어 pipe에 적합하다. 

```shell
$ ps -aux | grep java  # Java와 관련된 프로세스 찾기
```

### [2] Named Pipe 
- **양방향 통신이 가능하며, Pipe를 통해서, IPC가 가능하다.**
- mkfifo() 또는 mknod() 명령을 통해서 생성 가능하다.
- Anonymous Pipe 보다는 상대적으로 속도가 느리다.
- 간단한 IPC에 적합하다.
