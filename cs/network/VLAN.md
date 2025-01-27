# VLAN (Virtual LAN)
- L2Switch(주로 Distribution Switch)에서 제공하는 기능
- Network의 물리적 구성에 상관없이 논리적으로 분리하는 기술
- **Switch내에서 VLAN ID(12bit: 1~4094)로 그룹화하여 같은 물리적 네트워크에 연결된 장치들을 서로 다른 네트워크로 나눌 수 있다.**
- 다른 VLAN에 속한 장치들은 서로 통신할 수 없다.
  - 서로 다른 Broadcast Domain을 형성한다.
  - 각 VLAN에서 발생한 BroadCast는 독립적인 영역을 가지므로, 다른 VLAN에 영향을 주지 않는다.
- VLAN은 OSI 모델에서 **Layer2**에서 동작하며, 802.1Q라는 표준 프로토콜로 정의된다.


## 목적
### [1] Network 분리
- 물리적으로 연결되어있더라도, 논리적으로 분리
- ex) 회사 내부 네트워크와 Guest 네트워크를 분리

### [2] 보안 강화
- 서로 다른 VLAN은 기본적으로 통신이 불가능하기 떄문에, 민감한 데이터를 다루는 네트워크를 분리하여 보안 강화 가능
- ex) 회사 내부 네트워크와 Guest 네트워크를 분리

### [3] BroadCastDomain 분리
- BroadCast는 해당 Domain내부에서만 유효하다.
- BroadCast Storm을 방지하기 위해, BroadCastDomain을 분리한다.

## VLAN간 통신
- 같은 VLAN이라면, L2Switch로 통신이 가능하다.
- 다른 VLAN이라면, L3Switch, Router를 통해서 VLAN간 통신이 가능하다. (기본적으로는 차단)
