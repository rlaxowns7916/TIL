# TCP (Transmission Control Protocol)
## 1. 연결 지향(Connection-oriented)
- 3-Way Handshake로 세션을 맺고, 4-Way Handshake로 세션을 종료
- 세션을 맺을 때 서로 간의 초기 시퀀스 번호(Sequence Number), 옵션(MSS, Window Scale 등), 제어 정보를 교환하여 상태를 유지

## 2. 신뢰성 보장
- 오류 제어(Error Control), 재전송(Retransmission), 흐름 제어(Flow Control), 혼잡 제어(Congestion Control) 등을 통해 패킷 손실이나 중복 패킷이 발생해도 전체 데이터가 정확히 도착하도록 보장
- ARQ(Automatic Repeat reQuest) 기법을 적용해 패킷 손실 시 재전송을 수행
- SenderWindowSize = Min(ReceiverWindowSize, CongestionWindowSize)
  - ReceiverWindowSize: 수신측의 가용 버퍼 크기 (TCP 헤더의 Window Size 필드).
  - CongestionWindowSize: 네트워크 혼잡 상태에 따라 동적으로 조정되는 값.

## 3. Byte Stream
- 애플리케이션 계층에서 전달받은 데이터를 연속적인 Stream(Byte)으로 취급
- 실제 전송은 IP 계층에 **세그먼트(segment)**단위로 전달되지만, 애플리케이션은 ‘중간에 끊김 없이’ 데이터를 수신
- EOF(End of File) 개념과 유사한 FIN 플래그로 전송이 끝났음을 알림

## 4. 순서 보장
- Packet이 잘못된 순서로 도착해도, 시퀀스 번호를 통해 수신 측이 올바른 순서로 재조합하여 상위 계층에 전달

## 5. 오버헤드(Overhead)
- 연결 관리(3-Way/4-Way), 상태 유지를 위한 변수(시퀀스 번호, ACK 번호, 윈도우 크기 등) 및 혼잡/흐름 제어가 동작해야 하므로, 상대적으로 오버헤드가 크고 **전송 지연(latency)**이 발생할 수 있다.

## 주요 개념
### (0) ACK
- CACK (Cumulative Acknowledgement)
     - 현재까지 수신된 바이트들을 단 하나의 ACK로 일괄 확인응답 하는 것
        - TCP에서는, TCP 헤더의 32 비트 ACK 필드에서 이를 구현 
     - 중간 세그먼트 만 손실이면, 수신측은 그 뒤 세그먼트는 보관하고,
        - 현재까지 수신된 세그먼트 중 연속된 세그먼트로써 마지막 만 누적 확인응답 함
     - 만일, 비 연속적으로 수신된 세그먼트(여러 중간 손실)이면,  처음 중간 손실부터 다시 재전송 시작되는 등 심각한 성능 저하 발생

- SACK (Selective Acknowledgement)
     - 여러 세그먼트 중 손실된 세그먼트 만 선택적으로 확인응답하는 방식
     - TCP에서는, TCP 옵션으로 구현됨
        - 옵션필드에 구성, cAck와 배타적 개념 아님
        - 3way-handshake 송수신간에 SACK 사용 합의
        - TCP 세그먼트에 SACK 범위 목록을 포함시킴

### (1) 오류 제어(Error Control)
- **Checksum 으로 데이터 손상을 감지**
- ACK 기반으로 수신 성공 여부를 송신 측에 전달.
  - NAK개념은 존재하지 않으며, 일반적으로 일정 시간(RTO) 내 ACK가 오지 않으면 ‘손실’로 간주하고 재전송을 진행합니다.

### (2) 흐름 제어(Flow Control)
- Sliding Window 방식을 통해 수신 측이 처리 가능한 만큼만 보내도록 제어.
- 수신 측은 TCP 헤더의 Window Size 필드에 버퍼 여유분을 알려주고, 송신 측은 이 크기만큼만 전송
- WindowSize가 0이 되면(Zero Window), 송신 측은 전송을 잠시 멈추고, 수신 측이 WindowUpdate 를 보낼 때까지 대기
  - windowUpdate: 이전 ACK (zero-window)와 동일하지만, windowSize를 update하여 다시보낸 것

### (3) 혼잡 제어(Congestion Control)
- TCP Tahoe, Reno, CUBIC, BBR 등 다양한 알고리즘이 존재하며, 혼잡 상황을 예측·감지하여 송신 속도를 조절
- 전형적으로 Slow Start → Congestion Avoidance → Fast Retransmit/Recovery 순서를 거치며 윈도우를 동적으로 조정

### (4) 연결 수립 / 종료 절차
- 3-Way Handshake(연결 수립)
  - sequence, windowSize, MSS 등을 교환하며 연결을 맺는다.
  - ![3way-handshake](https://user-images.githubusercontent.com/57896918/159167298-8b71e1f5-6357-4236-bb0c-a47529d4556b.png)
- 4-Way Handshake(연결 해제)
  - 양방향(Full-duplex) half-close를 지원
  - ```text
        - 보내는 측에서는 남은 데이터를 마저 보낼 수 있어야 하기 떄문이다.
      - 만약 Client가 FIN을 보낸다면, 서버는 즉시 Fin을 보내지 않고, 데이터를 모두 보낸 후 Fin을 보낸다.
      - ```text
          [1] 클라이언트 → 서버 (FIN)
            - 클라이언트가 데이터를 보낼 게 없기 떄문에 FIN을 보냄.
            - 하지만 서버는 아직 클라이언트에게 보낼 데이터가 남아 있을 수도 있음.

          [2] 서버 → 클라이언트 (ACK)
            - 서버는 ACK를 보내면서 클라이언트의 종료 요청에 대해 수신했다고 응답함.
            - 서버는 연결을 완전히 닫지 않고, 남은 데이터를 계속 보낼 수 있는 상태.
    
          [3] 서버 → 클라이언트 (FIN)
            - 서버도 데이터를 다 보내고 나면 연결을 종료해도 된다고 FIN을 보냄.
    
          [4] 클라이언트 → 서버 (ACK)
            - 클라이언트가 서버의 FIN을 확인하고 ACK를 보냄.
            - 이후 클라이언트는 TIME_WAIT 상태로 일정 시간 대기 후 연결 종료.
        ```
    ```
  - ![4way handshake](https://user-images.githubusercontent.com/57896918/167259506-05d908f8-4b1d-43ac-8adf-2c0073e33b53.png)
- Active Close & Passive Close
  - 누가 Close 요청을 수행하는가에 따라 다르다.
    - FIN Flag를 보낸측이 Active, 받는 측이 Passive이다.


## TCP Header
![TCP Header](https://user-images.githubusercontent.com/57896918/159167310-845174f6-cf7a-47cc-86cf-b8cc2be6246c.png)
- 20Byte ~ 60Byte (Option 다 사용 시)
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
            - <img width="498" alt="없는 포트에 보냈을 때" src="https://github.com/user-attachments/assets/38f909b6-5132-49ef-bf14-48e7b4f19565">
        2. SEQ가 잘못 되었을 때
        3. 비정상적 연결 종료 (정상적이라면 FIN)
        4. (Optional) 정당한 사용자가 아닐 경우
    - **Java기준**
        - 상대방이 FIN을 보냈을 때
            - RST 수신 후, read() 호출 시 ==> SocketException: Connection Reset
            - RST 수신 후, write() 호출 시 ==> SocketException: Broken Pipe
        - 내가 FIN을 보냈을 때
            - RST 송신 후, read() 호출 시 ==> SocketException: Socket is closed
            - RST 송신 후, write() 호출 시 ==> SocketException: Socket is closed
- **PSH**
    - 데이터를 전송하겠다는 Flag이다.
    - 데이터를 전송하겠다는 쪽에서 보내는 Flag이다.
- **URG**
    - 긴급 데이터를 전송하겠다는 Flag이다.
- **PSH**
    - 빠른 데이터 처리를 위해서 데이터를 즉시 수신측의 어플리케이션으로 전달한다.

## 주요설정

### [1] idleTimeOut
- 특정 시간 동안 아무런 통신이 일어나지 않으면 TCP 연결을 자동으로 종료하는 기능
  - 유휴 자원을 정리하여 리소스 효율을 높이기 위함
- Application, Network 장비(FW, LB) 등에서 설정한다.

### [2] TCP KeepAlive
- 일정시간 동안 데이터가 없을 경우, 주기적으로 Packet을 보내 연결이 살아있는지 확인하는 기능
  - **물리적인 형태의 HealthCheck이다. (Application단의 HealthCheck는 Application의 정상적인 Up 확인)
- keepAlive 패킷을 보냈을 떄, 응답이 없다면 연결이 끊어졌다고 판단하고 종료한다.
- **상대방이 응답을 하면 연결유지, 응답이 없으면 일정 횟수 재시도 후 연결 종료**

### FIN vs RST
```text
[RFC 1122]

A TCP connection may terminate in two ways: 
(1) the normal TCP close sequence using a FIN handshake, 
(2) an "abort" in which one or more RST segments are sent and the connection state is immediately discarded.
```

## TimeOut

### [1] ConnectionTimeOut
- 3Way-HandShake를 통해서 연결을 맺는 과정에서 TimeOut이 발생한다.

### [2] ReadTimeOut (= SocketTimeOut)
- read()를 시작 한 후 , 일정 시간 이상 Response를 받지 못할 경우 발생한다.

***

## 오류제어 (Error Control)
- 오류가 발생하면 Retransmission 을 통해서 신뢰성 있는 통신을 보장한다.
- Retransmission 은 비효율적이기 떄문에 적을수록 좋다.
- 시간이지나도 ACK가 도착하지 않으면, 오류라고 가정하고 재전송한다.
- 오류 검출 (Detection) 과 재전송 (Retransmit)을 포함한다.
- ARQ(Automatic Repeat reQuest) 를 통해서 자동으로 재전송을 통해서 오류를 복구한다.

### 1. Stop And Wait ARQ
- Packet을 전송하고 ACK가 올 때까지 대기한다. 
  - ACK를 받아야지 다음 Packet 전송이 가능하다.
- ACK에 수신에 대한 TimeOut이 발생하면 재전송 한다. (RTO)
- 수신측 WinowSize가 1이다.
- 효율이 떨어지기 떄문에 현재는 거의 사용되지 않는다. (TimeOut 까지 대기하기 때문)
![stop   wait](https://user-images.githubusercontent.com/57896918/163091486-c7b440c8-48e5-459c-adc2-8ce1894aa3b8.png)

### 2. Go Back N ARQ
- Stop And Wait ARQ의 효율을 높이기 위해서 만들어진 기법이다.
- **송신 측은 수신측의 ACK를 기다리지 않고 여러개의 Packet을 보낼 수 있다.**
  - 송신측은 SlidingWindow를 이용하여 여러개의 Packet을 보낼 수 있다.
  - 수신측은  WinowSize가 1이다.
- **전송된 프레임이 손상되거나 유실될 경우, 마지막으로 ACK를 받은 프레임 이후로 모두 다시 재전송하는 기법이다.** (GoBackN)
  - 수신측은 패킷을 순서대로 수신해야 하며, 손실된 패킷 이후의 패킷이 도착하면 폐기.
  - 수신측은 손실된 패킷을 받을 때까지 손실된 지점 이후의 ACK을 송신하지 않음.
  - 송신측은 손상된 이후의 Packet부터 모두 다시 보낸다.
- **손실 지점 이후의 Packet을 모두 재전송해야 하기 때문에 비효율 적이다.**

![GBN](https://user-images.githubusercontent.com/57896918/163091500-11209de5-ad97-49f8-ace4-6429e0e8708c.png)


### 3. Selective Repeat ARQ
- Go Back N ARQ의 효율을 높이기 위해서 만들어진 기법이다. )
- **손상되거나 분실된 Packet만을 재전송한다.**
- 수신측에서 별도의 데이터정렬을 수행한다.
    - 별도의 버퍼를 필요로 한다.
    - 수신측도 WindowSize를 가지고 있다.
    - 폐기를 하지 않기때문에 재정렬을 수행할 버퍼가 필요한 것이다.
    - 손실 지점 이후의 Packet은 Buffer에 저장하고, 손실된 Packet을 수신하였을 경우 재정졀하여 처리한다.
- **손상된 패킷만 재전송하기 때문에 효율적이다. (주로 사용)**

![SR](https://user-images.githubusercontent.com/57896918/163091673-f3eb544f-39f3-4f3d-b0e6-f8df0a0213b1.png)


***

## 흐름제어 (Flow Control)
- ZeroWindow 상태를 방지하기 위해서 수신측에서 속도를 제어한다. (자신의 WindowSize를 통해서)
  - **수신측이 처리할 수 있는 양을 기반**
- 송신 측의 송신속도를 제어한다.
  - 송,수신 측 사이에서 패킷 수를 조절 하는 것이다.
- WindowSize를 가지고 조절한다.
    - 수신측에서 자신이 얼마만큼의 데이터를 받을 수 있는지를 알려준다.
    - 송신측은 ACK를 받을 떄마다, Window가 앞으로 Sliding 하여, 새로운 데이터를 보낼 여유를 가지게된다.

***

## 혼잡제어 (Congestion Control)
- 네트워크 내(Router, Switch, ...) 의 패킷 수를 조절하는 것이다. (혼잡을 발생하게 하지 않기 위해서)
  - **Network Traffic 혼잡을 통해서 판단**
  - Network 과부하를 방지
- 여러가지 알고리즘 (Tahoe, Reno, BBR, ...)을 통해서 동적으로 조절한다.
- **PacketLoss, RTT 증가 등을 혼잡 징후로 판단해 송신속도를 조절한다.**
- **Packet Loss와 Retransmission이 증가하면 송신측도 효율이 떨어지기 떄문이다.**
- **CongestionWindowSize를 조절한다.**
    - 변수로 가지고 있으며, 현재 Network에서 허용할 수 있는 최대 전송량을 수정한다.

### [1] SlowStart
- **PacketLoss를 RTO를 통해서 판단한다.**
- 초기 연결 시, 혹은 혼잡이 발생했을 시에 사용된다.
- CongestionWindowSize를 T지수적으로 (Exponential)적으로 cwnd를 늘려가본다.
  - 초기 cwnd=1 MSS(1 세그먼트 크기)로 시작하여, ACK를 받을 때마다 cwnd를 1 세그먼트 단위씩 2배 증가시킵니다.
- 혼잡이 감지되면 CongestionWindowSize를 1로 낮춘다.

### [2] AIMD(Additive Increase Multiplicative Decrease)
- Additive Increase: 매 RTT당 cwnd를 1 MSS씩 선형 증가 (예: 10 → 11 → 12 MSS). 
- Multiplicative Decrease: 패킷 손실 시 cwnd를 반으로 감소 (예: 20 → 10 MSS).

### [3] FastRetransmit
- 단일 Packet손실을 TimeOut 없이 신속히 복구한다.
- 3개의 중복 ACK를 수신할 때, 손실된 패킷으로 판단하고 빠르게 재전송한다.
- 그 이후 FastRecovery로 이어진다. (SlowStart (X))

### [4] FastRecovery
- FastRetransmit 이후 전송속도를 떨어뜨리지 않고 안정화하는게 목적이다.
- SlowStart가 아닌 AIMD를 사용한다.
