# TCP 4-Way Handshake

## 1. 개요
- TCP 연결을 안전하게 종료하기 위한 표준 메커니즘 
- 4단계가 필요한 이유는 양방향 데이터 전송을 독립적으로 종료해야 하기 때문

## 2. 4-Way Handshake 시각화

```
┌─────────────┐                                    ┌─────────────┐
│   Client    │                                    │   Server    │
│(Active Close)│                                   │(Passive Close)│
└─────────────┘                                    └─────────────┘
       │                                                   │
       │                   1. FIN (seq=x)                  │
       │ ─────────────────────────────────────────────────▶│
       │ ESTABLISHED → FIN_WAIT_1                          │ ESTABLISHED
       │                                                   │
       │                   2. ACK (ack=x+1)                │
       │ ◀─────────────────────────────────────────────────│
       │                                                   │ ESTABLISHED → CLOSE_WAIT
       │ FIN_WAIT_1 → FIN_WAIT_2                          │
       │                                                   │
       │              [서버 애플리케이션 데이터 처리]        │
       │                                                   │
       │                   3. FIN (seq=y)                  │
       │ ◀─────────────────────────────────────────────────│
       │                                                   │ CLOSE_WAIT → LAST_ACK
       │ FIN_WAIT_2                                        │
       │                                                   │
       │                   4. ACK (ack=y+1)                │
       │ ─────────────────────────────────────────────────▶│
       │ FIN_WAIT_2 → TIME_WAIT                            │ LAST_ACK → CLOSED
       │                                                   │
       │ [2MSL 대기]                                       │
       │                                                   │
       │ TIME_WAIT → CLOSED                                │
       │                                                   │
```

## 3. 각 단계별 상세 설명

### 단계 1: ActiveCloser의  FIN
- Active Closer가 FIN 플래그가 설정된 세그먼트 전송
  - 더 이상 보낼 데이터가 없다는 것을 의미
- 상태 변화: `ESTABLISHED` → `FIN_WAIT_1`

### 단계 2: PassiveCloser의 ACK
- Passive Closer가 FIN에 대한 ACK 전송
  - 상대방이 더이상 보낼 데이터가 없다는 것을 인지
- 상태 변화:
    - PassiveCloser: `ESTABLISHED` → `CLOSE_WAIT`
    - ActiveCloser: `FIN_WAIT_1` → `FIN_WAIT_2`
- 이 시점에서 연결은 **Half-Closed** 상태

### 단계 3: PassiveCloser의 FIN
- PassiveCloser가 모든 데이터 전송 완료 후 자신의 FIN 전송
  - 더 이상 보낼 데이터가 없다는 것을 의미
- 상태 변화: `CLOSE_WAIT` → `LAST_ACK`

### 단계 4: ActiveCloser의 ACK
- Active Closer 가 마지막 ACK 전송
- 상태 변화:
    - Active Closer: `FIN_WAIT_2` → `TIME_WAIT`
    - Passive Closer: `LAST_ACK` → `CLOSED`

## 4. TCP 상태 전이 다이어그램

```
[ESTABLISHED] ─────┐
     │             │
     │ close()     │ FIN 수신
     ▼             ▼
[FIN_WAIT_1]   [CLOSE_WAIT]
     │             │
     │ ACK 수신    │ close()
     ▼             ▼
[FIN_WAIT_2]   [LAST_ACK]
     │             │
     │ FIN 수신    │ ACK 수신
     ▼             ▼
[TIME_WAIT]    [CLOSED]
     │
     │ 2MSL 경과
     ▼
[CLOSED]
```

## 5. TIME_WAIT 상태의 중요성

### 목적
1. **지연된 세그먼트 방지**: 이전 연결의 패킷이 새 연결에 혼입되는 것을 방지 (FIN이 아닌 실제 데이터)
2. **최종 ACK 보장**: Passive Closer 가 안전하게 연결을 종료할 수 있도록 보장
   - Passive Closer 입장에서 ACK를 받지못하면, LAST_ACK 상태가 유지되면서 재시도 할 것이기 때문
    - ```text
      ┌─────────────┐                                    ┌─────────────┐
      │   Client    │                                    │   Server    │
      │(Active Close)│                                   │(Passive Close)│
      └─────────────┘                                    └─────────────┘
            │                                                   │
            │                   3. FIN (seq=y)                  │
            │ ◀─────────────────────────────────────────────────│
            │                                                   │ LAST_ACK
            │                                                   │
            │                   4. ACK (ack=y+1)                │
            │ ─────────────────────X (패킷 유실!)               │
            │ TIME_WAIT                                         │ 여전히 LAST_ACK
            │                                                   │
            │                                                   │
            │              [서버: ACK를 못 받음!]                │
            │              [타임아웃 후 FIN 재전송]              │
            │                                                   │
            │                   FIN 재전송 (seq=y)              │
            │ ◀─────────────────────────────────────────────────│
            │                                                   │
            │ TIME_WAIT 상태이므로                              │
            │ ACK를 다시 보낼 수 있음!                          │
            │                                                   │
            │                   ACK 재전송 (ack=y+1)            │
            │ ─────────────────────────────────────────────────▶│
            │                                                   │ LAST_ACK → CLOSED
       ```

### 지속 시간
- **2MSL (Maximum Segment Lifetime)**: 일반적으로 60초~240초
  - msl: 최대 세그먼트 생존 시간 (TTL과 다르게, 지연까지 포함한 보수적인 수치 산정이다.)
- Linux 기본값: 60초 (MSL = 30초)

## 개발 주의 사항
**Server가 ActiveCloser가 되서는 안된다**
- TIME_WAIT 때문에, bind가 불가능하기 떄문이다.
- 아래와 같은 방법으로 해결이 가능하다.
  1. Client가 ActiveCloser가 되도록 구현
  2. Connection Pooling 사용