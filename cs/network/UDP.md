# UDP (User Datagram Protocol) 핵심 요약
1. 기본 특성
   - 비연결형 (Connectionless): 사전 연결 수립 없이 데이터 전송.
   - 신뢰성 없음 (Unreliable): 패킷 손실, 중복, 순서 변경 가능.
   - 경량화 (Lightweight): 헤더 오버헤드 작음 (고정 8바이트).
   - 비동기 통신: 수신측의 확인 응답(ACK) 없이 단방향 전송.

2. 주요 장점
   - Low Latency: 연결 설정/해제 절차 없음 → 빠른 전송. 
   - 단순성: 혼잡 제어, 재전송, 순서 보장 미구현 → 낮은 CPU/메모리 사용. 
   - BroadCast / MultiCast 지원: 동시 다수 대상 전송 가능.

3. 주요 단점 
   - 패킷 손실 가능성: 재전송 메커니즘 없음. 
   - 데이터 무결성 보장 불가: 
     - Checksum이 Optional하다
     - 오류 시 패킷 폐기 
   - 혼잡 제어 미적용: 네트워크 과부하 시 전체 성능 저하 가능성.

4. 사용 사례 
   - 실시간 애플리케이션: 영상/음성 스트리밍 (예: Zoom, YouTube Live). 
   - 온라인 게임: 빠른 반응 속도 요구 (예: FPS, MOBA). 
   - DNS 쿼리: 단순한 요청-응답 및 작은 데이터 크기. 
   - IoT/센서 데이터: 주기적 상태 보고 (예: 온도 센서). 
   - QUIC (HTTP/3): TCP 대체하여 UDP 기반 고속 통신.

## UDP Header
![UDP Header](https://user-images.githubusercontent.com/57896918/159167336-e49ca39d-79fc-480f-a5d8-f96cb93a087f.png)

## VS TCP
|         | UDP        | TCP                      |
|----------|------------|--------------------------|
| 속성      | 비연결형       | 연결 지향형 (3-Way Handshake) |
| 신뢰성     | 없음         | 보장 (재전송, 순서 복원)          |
| 속도      | 빠름         | 상대적 느림 (오버헤드 ↑)          |
| 헤더 크기  | 8바이트       | 20~60바이트                 |
| 혼잡 제어  | 없음         | AIMD, Slow Start 등 적용    |
| 사용 예시  | 실시간 통신, DNS | 파일 전송, 이메일, 웹            |



