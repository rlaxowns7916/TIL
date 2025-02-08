# Network Topology 구조
- Network 장치 (Server, Switch, ...)가 어떻게 연결되어있는지 나타내는 방식
- 구축하고자 하는 Network에 따라 필요한 장비의 성능, 수량, 확장성등을 고려하여 알맞은 Topology를 선택해야한다.

## [1] Bus Topology
- 모든 Node(장치)가 하나의 공용회선(Bus)에 연결된 형태
  - Network에서 하나의 장치가 Data를 보내면, 모든 장치가 동시에 신호를 받는다.
  - 소규모 Network 운영에 적합하다.
- 장점
  - 설치가 간단하며 비용이 저렴하다.
- 단점
  - Network가 커질 수록 성능이 저하된다.
  - Collision 확률이 높다.
  - Backbone이 고장나면 Network 전체가 중단된다.

## [2] Ring Topology
- 모든 Node(장치)가 순환형태로 연결된 형태
- 데이터는 단방향, 양방향 모두로 흐를 수 있다.
- **Token Passing** 방식을 사용하여 충돌을 방지한다.
  - 데이터를 보내기 위해서는 Token을 받아야 한다.
- 장점
  - Collision이 발생하지 않는다. (Token Passing 방식 덕분)
- 단점
  - 하나의 Node가 고장나면 Network 전체가 중단된다. (양방향 Ring구조를 사용하면 보완 가능하다.)

## [3] Star Topology
- 중앙 Node(Hub, Switch)에 모든 Node(장치)가 연결된 형태
- Hub와 Switch가 Network의 핵심역할을 한다.
- 장점
  - Network의 확장성이 좋다.
  - 장애가 특정 Node에만 국한된다. (격리성)
  - Bus형, Ring형 보다 성능이 뛰어나다.
- 단점
  - 중앙Node (Hub,Switch) 가 고장나면 Network 전체가 중단된다.

## [4] Tree Topology
- Star Topology + Bus Topology
- **계층적 구조를 가지며, RootNode에서 여러 SubNode로 확장된다**
  - 여러개의 StarNetwork가 BusNetwork 방식으로 연결된 형태
- 장점
  - 확장성이 뛰어나다.
  - 대규모 Network에서 안정적으로 운영 가능하다.
- 단점
  - Start와 동일하게, 중앙Node의 장애시 하위 Network 전체가 마비 될 수 있다.
