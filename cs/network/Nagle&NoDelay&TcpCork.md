# Nagle vs NoDelay & TCP Cork
- 애플리케이션의 send() 호출과 실제 네트워크 패킷 전송은 1:1로 대응되지 않는다.
- TCP가 내부적으로 버퍼링을 사용하기 때문
  - ReadBuffer: NIC로 부터 받은 데이터를 저장
    - TCP default: 128KB
  - WriteBuffer: 애플리케이션에서 SystemCall(write())한 데이터를 저장
    - TCP default: 128KB

```text
Application Layer:  send("H") -> send("e") -> send("l") -> send("l") -> send("o")
       ↓
TCP Send Buffer:    [H][e][l][l][o] (버퍼에 누적)
       ↓
Network Layer:      단일 패킷으로 전송: [Hello]
```

## [1] Nagle
- 작은 패킷들을 모아서 한 번에 전송함으로써 네트워크 효율성을 높이는 기법
- **Default로 활성화되어 있다.**
- 아래의 로직으로 동작
   1. 첫 번째 데이터는 즉시 전송
   2. 이후 데이터는 다음과 같은 조건을 만족할 때 까지 대기
      - MSS 만큼 데이터가 쌓임
      - 이전 Packet의 ACK가 도착
      - 더 이상 보낼 데이터가 없음


## [2] NoDelay
- 소켓 옵션을 설정하면 Nagle 알고리즘을 비활성화하여, 데이터를 즉시 전송
- Nagle을 비활성화 하는 옵션

## [3] TCP Cork
- Nagle과 유사하게 작은 패킷들을 모아서 한 번에 전송하는 기법
- Nagle과의 차이점은, TCP Cork는 수동이라는 것
  - Cork ON: Buffer에 데이터를 모으는 상태
  - Cork OFF: Buffer에 모인 데이터를 즉시 전송하는 상태
- setSockOpt로 설정 가능
  - I/O 횟수는 줄겠지만, SystemCall이 늘어난다는 것은 생각해볼 포인트
