# Routing
- Routing Table에 계산해놓고 Routing 할 대상을 결정한다.
  - Metric(비용) 기반으로 경로를 결정한다.
- Routing 대상으로 패킷을 전송한다.

## Routing 정보 갱신 방식

### 1. Static Routing Protocol (정적 라우팅 프로토콜)
- 수동으로 사람이 라우터 경로를 입력하는 것이다.
- 관리자의 부담이 가중되며, 경로에 이상이 있을 시에, Routing이 불가능하다.
- 소규모 네트워크에 적합하다.

### 2. Dynamic Routing Protocol (동적 라우팅 프로토콜)
- Router가 동적으로 경로를 판단한다.
- RIP, IGRP, OSPF, EIGRP 가 있다.
- 관리자의 부담이 줄어든다.
- 대규모 네트워크에 적합하다.

## Routing

### 1. Interior Gateway Protocol (내부 네트워크)
- 같은 관리하의 있는 내부 네트워크 집합 사이의 Routing 방법이다.
- RIP, IGRP, OSPF, EIGRP

### 2. Exterir Gateway Protocol (외부 네트워크)
- 다른 관리자의 관리하에 있는 외부 네트워크 사이의 Routing 방법이다.
- BGP, EGP


## Routing Algorithm

### 1. Distance Vector Algorithm
- RIP, EGP
- 분산형 알고리즘이다.
- 이웃 Router끼리의 정보를 교환하여 정보를 갱신한다. (Bellman-Ford Algorithm)
- 주기적으로 Routing 데이터를 교환한다.

### 2. Link State Algorithm
- OSPF
- 중앙 집중형 알고리즘이다.
- 이벤트 기반으로 데이터를 교환한다.
- 모든 Router가 모든 Link의 비용을 알고 있기 떄문에, Dijkstra Algorithm을 통해서 최적의 경로를 계산 할 수 있다.