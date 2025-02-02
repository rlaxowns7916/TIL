# 1. LostSegment
- 실제로 Packet이 손실되었거나, 오랫동안 ACK를 받지 못해 재전송이 필요하다고 판단하는 상태이다.

## 원인
1. Packet Loss
   - Network 혼잡 (Congestion), 무선 환경 등 다양한 이유로 Packet이 중간 경로에서 유실 될 수 있다.
2. Error
    - 오류 검출로 인해서 잘못 전송된 Packet이 폐기되었을 수 있다.
3. Retransmit Delay
   - 재전송이 수행되었지만, 모종의 이유로 ACK가 늦게 도착했을 수 있다.

***

# 2. ReTransmit
- 신뢰성있는 TCP 통신에서, 손실되었다고 의심되는 Packet을 다시 보내는 매커니즘을 의미한다.

## 원인
1. Retransmission TimeOut
    - RTO(전송한 Packet의 응답을 수신할 떄 까지 기다리는시간)를 초과했을 시, 재전송을 실시한다.
2. Fast Retransmit
    - 수신 측에서, 동일한 ACK번호를 여러번 받으면 (ACK Duplication) 재전송을 수행한다.
***

# 3. Out of Order
- 이전에 받았던 Packet들과의 Sequence순서가 정상적으로 유지되지 않은 것을 의밓나다.
- **내부적으로 ReceiveBuffer에 저장해둔 뒤, 올바른 순서가 될 떄 까지 기다렸다가 Application에 전달한다.**

## 원인
1. Network 경로 차이
   - MultiPath Routing 혹은 Swith, Router 내부에서 다르게 처리되어 일부 패킷이 우회경로로 더 빠르게 혹은 느리게 전달되는 경우
2. 대역폭, QoS 문제
   - 일부 트래픽이 우선적으로 처리됨에 따라 순서가 엇갈릴 수 있다.
3. 송신 측 문제
    - 송신측의 Segment분할 로직에 문제가 있거나, 비정상적으로 송신되는 알고리즘을 가진 경우 (정상적인 상황이 아님)

***
# 4. Zero Window (엄밀히 말하면 장애라고는 할 수 없음)
- TCP 통신에서 수신측은 데이터를 더 받을 여유상황이 아니라면, Window크기를 0으로 Set하여 송신측에 알린다.
  - 다시 수신을 재개 할 수 있을 떄는 WindowSize가 0이아닌 동일한 ACK를 보내서 알린다. (window update)
- 일종의 "보내지 말라"라는 신호이며, 수신측의 ReceiveBuffer가 가득 찬 것이다.