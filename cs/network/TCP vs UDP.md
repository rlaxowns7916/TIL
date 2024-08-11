#  TCP (Transmission Control Protocol)
- EndPoint간 **신뢰성 있는** 데이터 전송을 담당하는 계층이다.
  - 포트번호에 해당하는 Process에게 데이터를 전달한다.
- PDU로 Segment를 사용한다.
- 연결 지향형이다.
  - 3Way-HandShake를 통한 통신 연결을 하고, 4way-HandShake로 연결을 해제한다.
- Byte Stream
   - 연속적인 Byte 열을 보낸다.
- 데이터의 순차적인 전송을 보장한다.
  - 순서보장을 위한 **가상회선 방식**을 사용한다.
  - 각 패킷에 가상회선 식별자가 포함되며, 모두 전송될 경우 가상회선이 해제된다.
- 오류를 제어한다.
- 흐름을 제어한다.
- 혼잡을 제어한다.

### nc를 이용한 간단한 TCP 서버/클라이언트 구현
```shell
(optional)
# nc 설치
brew install netcat

# 서버
nc -lk 1234

# 클라이언트
nc -v localhost 1234

# tcp dump (1234 포트, any 인터페이스, 사람이 읽을수 있게, 실시간으로 출력)
sudo tcpdump -i any -lA -nn tcp and port 1234


```


## TCP Header
![TCP Header](https://user-images.githubusercontent.com/57896918/159167310-845174f6-cf7a-47cc-86cf-b8cc2be6246c.png)

### TCP Flag

- **SYN**
  - 연결을 요청하는 Flag이다.
  - 연결을 요청하는 쪽에서 보내는 Flag이다.
- **ACK**
  - 수신측에서 데이터를 받았다는 Flag이다.
  - 데이터를 받았다는 Flag이다.
- **FIN**
  - 정상적으로(gracefully) 연결을 종료하겠다는 Flag이다.
  - 연결을 종료하겠다는 쪽에서 보내는 Flag이다.
- **RST**
  - 일종의 강제종료이다. 
  - 비정상적인 연결을 종료한다.
  - 이 Flag가 설정된 패킷을 수신하면 연결이 즉시 종료된다.
  - (예시)
    1. 없는 Port에 요청을 보냈을 때
    2. SEQ가 잘못 되었을 때
    3. 비정상적 연결 종료 (정상적이라면 FIN)
- **PSH**
  - 데이터를 전송하겠다는 Flag이다.
  - 데이터를 전송하겠다는 쪽에서 보내는 Flag이다.
- **URG**
  - 긴급 데이터를 전송하겠다는 Flag이다.
- **PSH**
  - 빠른 데이터 처리를 위해서 데이터를 즉시 수신측의 어플리케이션으로 전달한다.


### FIN vs RST
```text
[RFC 1122]

A TCP connection may terminate in two ways: 
(1) the normal TCP close sequence using a FIN handshake, 
and (2) an "abort" in which one or more RST segments are sent and the connection state is immediately discarded.
```

## 연결지향

### 3Way-HandShake
- TCP에서 연결을 맺는 과정이다.
- 아래와 같은 순서이다.
  - -> syn
  - <- syn + ack
  - -> ack
![3way-handshake](https://user-images.githubusercontent.com/57896918/159167298-8b71e1f5-6357-4236-bb0c-a47529d4556b.png)

### 4way-CloseHandShake
- TCP에서 연결을 끊는 과정이다.
- 아래와 같은 순서이다.
  - -> fin
    - 클라이언트가 서버에 연결을 끊겠다는 신호를 보낸다. (FIN flag)
  - <- ack
    - 서버가 연결을 CLOSE-WAIT로 변경하고, 클라이언트에게 ACK를 보낸다.
  - <- fin
    - 서버가 클라이언트에게 연결을 끊겠다는 신호를 보낸다.
  - -> ack
    - 클라이언트가 서버에게 ACK를 보낸다.
![4way handshake](https://user-images.githubusercontent.com/57896918/167259506-05d908f8-4b1d-43ac-8adf-2c0073e33b53.png)

### 가상회선
- 3Way-HandShake를 통해서 생성된 논리적인 연결이다.
- 각 패킷에는 가상회선 식별번호가 포함된다.
- 최초 패킷 전송 떄 경로가 결정되고 그 이후로 똑같은 경로로 순서대로 전송된다.

## 오류제어
- 오류가 발생하면 재전송을 통해서 신뢰성 있는 통신을 보장한다.
- 재전송은 비효율적이기 떄문에 적을수록 좋다.
- ACK를 통해 정상 수신했음을 알린다.
- 시간이지나도 ACK가 도착하지 않으면, 오류라고 가정하고 재전송한다.
- 오류 검출 (Detection) 과 재전송 (Retransmit)을 포함한다.
- ARQ(AutomaticRepeatreQuest) 를 통해서 자동으로 재전송을 통해서 오류를 복구한다.

### 1. Stop And Wait ARQ
- 패킷을 전송하고 ACK가 올 때까지 대기한다.
- 수신측에서 데이터를 받지못하면 NAK을 전송한다.
- NAK을 수신한 송신측은 재전송한다.
- 타임아웃이 발생하면 일정기간을 두고 재전송한다.
- ACK 혹은 NAK를 받을 때 까지 데이터를 전송하지 않기 떄문에 전송효율이 떨어진다.
- 
![stop   wait](https://user-images.githubusercontent.com/57896918/163091486-c7b440c8-48e5-459c-adc2-8ce1894aa3b8.png)

### 2. Go Back N ARQ
- 전송된 프레임이 손상되거나 유실될 경우, 마지막으로 ACK를 받은 프레임 이후로 모두 다시 재전송하는 기법이다.
- NAK 수신
- TimeOut
- 송신측의 프레임 유실 (수신측에서 프레임 1, 3을 받을 경우 3을 폐기하고 NAK2를 전송한다.)

![GBN](https://user-images.githubusercontent.com/57896918/163091500-11209de5-ad97-49f8-ace4-6429e0e8708c.png)


### 3. Selective Repeat ARQ
- GoBackN의 단점을 개선한 것이다.
- 손상되거나 분실된 프레임만을 재전송한다.
- 별도의 데이터정렬을 수행한다.
  - 별도의 버퍼를 필요로 한다.
  - 폐기를 하지 않기때문에 재정렬을 수행할 버퍼가 필요한 것이다.

![SR](https://user-images.githubusercontent.com/57896918/163091673-f3eb544f-39f3-4f3d-b0e6-f8df0a0213b1.png)

**결론: Network 재전송보다, Buffer 재정렬이 더 간편하기 때문에 Selective Repeat를 주로 사용한다고 한다.**
## 흐름제어
- 송신 측의 송신속도를 제어한다.
- 송,수신 측 사이에서 패킷 수를 조절 하는 것이다.
- WindowSize를 가지고 조절한다.
  - 수신측으로부터 ACK가 오면 Window를 옆으로 옮겨서 다음 패킷을 전송한다.
- 수신 측의 수신속도가 송신측의 송신속도보다 느리면 문제가 발생 할 수 있다.

### Stop & Wait
- 매번 전송한 패킷에 대해 확인 응답을 받아야 다음 패킷을 전송한다.

### SlidingWindow
- 수신측에서 설정한 WindowSize만큼 송신측에서 송신한다.
- 동적으로 데이터의 흐름을 제어할 수 있다.


## 혼잡제어
- 네트워크 내의 패킷 수를 조절하는 것이다.
- 네트워크가 처리 할 수있는 양 이상의 패킷이 전달될 경우 Overflow가 일어나므로, 이를 방지 하는 것이다.

### SlowStart
- CongestionWindowSize를 ThreshHold까지 2배씩 늘린다.
- CongestionWindowSize가 ThreshHold에 도달하면 1씩 증가시킨다.
- 혼잡이 감지되면 CongestionWindowSize를 1로 낮춘다.

### FastRetransmit
- 수신측에서 순서대로 패킷을 받지 못했을 때 발생한다.
- 수신측에서는 정상적으로 받은 마지막 패킷의 ACK를 송신한다.
- 송신측은 누락을 확인하고 다시 재전송한다.
    - TimeOut까지 기다리지 않고 재전송한다.

### FastRecovery
- SlowStart가 1까지 내려갔다가 다시 올라오는게 시간이 오래걸리기 떄문에 보완한 것이다.
- 혼잡이 발생하면 CongestionWindowSize를 절반으로 줄이고 1씩 증가시킨다.


# UDP (UserDatagramProtocol)
- PDU로 DataGram을 사용한다.
- 비연결 지향 방식이다.
  - 전송 순서가 일정하지 않을 수 있다.
    - 갓 패킷이 독립적으로 최적의 경로를 선택한다.
  - 수신 여부를 확인하지 않는다.
- 신뢰성이 떨어진다.
  - 데이터 유실이 있을 수 있다.
- 속도가 빠르다.
- 실시간 스트리밍등에 주로 사용된다.
- 헤더가 TCP에 비해 간소화 되어 있다.

## UDP Header
![UDP Header](https://user-images.githubusercontent.com/57896918/159167336-e49ca39d-79fc-480f-a5d8-f96cb93a087f.png)

