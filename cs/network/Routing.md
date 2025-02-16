# Routing
- Routing Table에 계산해놓고 Routing 할 대상을 결정한다.
  - Metric(비용) 기반으로 경로를 결정한다.
- Routing 대상으로 패킷을 전송한다.
- Router와 L3Switch는 다르다.

## Routing 정보 갱신 방식

### 1. Static Routing Protocol (정적 라우팅 프로토콜)
- 수동으로 사람이 라우터 경로를 입력하는 것이다.
- 관리자의 부담이 가중되며, 경로에 이상이 있을 시에, Routing이 불가능하다.
- 소규모 네트워크에 적합하다.

### 2. Dynamic Routing Protocol (동적 라우팅 프로토콜)
- Router들이 서로 정보를 교환하여 동적으로 경로를 판단한다.
  - Netowork의 변화에 따라 경로를 자동으로 수정한다.
- RIP, IGRP, OSPF, EIGRP 가 있다.
- 관리자의 부담이 줄어든다.
- 대규모 네트워크에 적합하다.

## Routing

### 1. Interior Gateway Protocol (내부 네트워크)
- 같은 관리하의 있는 내부 네트워크 집합 사이의 Routing 방법이다.
- RIP, IGRP, OSPF, EIGRP

### 2. Exterior Gateway Protocol (외부 네트워크)
- 다른 관리자의 관리하에 있는 외부 네트워크 사이의 Routing 방법이다.
- IGP보다 속도는 느리지만, 대용량의 Router정보를 교환할 수 있다.
- BGP(사실상 표준 / Path Vector Algorithm)

| 구분                  | IGP (Interior Gateway Protocol)                 | EGP (Exterior Gateway Protocol)                  |
|-----------------------|------------------------------------------------|-------------------------------------------------|
| **사용 영역**          | 자율 시스템(AS) 내부의 라우팅을 담당              | 자율 시스템(AS) 간의 라우팅을 담당                |
| **대표 프로토콜**      | OSPF, EIGRP, RIP                               | BGP                                              |
| **라우팅 정보 교환**   | 동일한 관리 도메인 내에서 라우팅 정보 교환           | 서로 다른 관리 도메인(자율 시스템) 간에 라우팅 정보 교환 |
| **경로 선택 기준**     | 주로 홉 수, 대역폭, 지연 시간 등 **거리 기반**      | **정책 기반** (경로 속성, AS 경로, 비용 등 다양한 기준) |
| **확장성**             | 상대적으로 제한된 크기의 네트워크에 적합             | 매우 큰 규모의 네트워크(인터넷)에서 사용 가능         |
| **복잡성**             | 비교적 단순하고 빠른 수렴을 제공                     | 경로 정책이 복잡하고 수렴 속도가 느림                   |
| **트래픽 제어**        | 네트워크 내부에서 라우팅 정책을 제어                  | 네트워크 간의 상호 연결 정책을 기반으로 경로 선택       |
| **적용 예**            | 기업, 캠퍼스 네트워크 등 자율 시스템 내부 라우팅      | 인터넷 상의 ISP 간 경로 선택, 글로벌 네트워크 라우팅     |



## Routing Algorithm

### 1. Distance Vector Algorithm
- RIP, EGP
- 분산형 알고리즘이다.
- 이웃 Router끼리의 정보를 주기적으로 교환하여 정보를 갱신한다. (Bellman-Ford Algorithm)
  - Hop Count를 기반으로 최적의 경로를 계산한다.
  - 주기적으로 Routing 데이터를 교환한다.
  - Network 크기가 커지면 느려질 수 있다.
- 소규모 Network에 적합하다.

### 2. Link State Algorithm
- OSPF
  - HopCount제한이 없다.
- 중앙 집중형 알고리즘이다.
- Router가 전체 Network에 대한 정보를 가지고 있으며, Dijkstra Algorithm을 통해서 최적의 경로를 계산 할 수 있다. 
  - 지역내의 모든 Router에 변경이 발생했을 때 Flooding하고, Routing테이블을 구성, 계산한다.
- 대규모 Netowkr에 적합하며, 트래픽변화에 신속하게 대응 가능하다.

### 3. Path Vector Algorithm
- BGP
- AS 경계에서 사용되는 알고리즘이다.
- AS 경계에서 경로를 교환하며, 경로 속성을 기반으로 최적의 경로를 선택한다.
- 경로 속성, AS 경로, 비용 등 다양한 기준을 기반으로 경로를 선택한다.
  - 정책을 정할 수 있다.
- 인터넷 상의 ISP 간 경로 선택, 글로벌 네트워크 라우팅에 사용된다.