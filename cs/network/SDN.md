# SDN (Software Defined Network)
- Network제어, 데이터전달 기능을 분리하여 **중앙에서 관리할 수 있도록 하는** 네트워크 아키텍쳐
- Network장비는 H/W와 S/W가 강결합되어있어서 설정이 복잡하며 변경이 어려웠지만, SDN은 유연하게 네트워크를 제공할 수 있게 해준다.
- OpenStack, K8s등과 통합하여 Network를 유연하게 운영 할 수 있다.

## 핵심개념
### [1] Control Plane, Data Plane 분리
- 기존 Network 장비(Router, Switch, ...)는 제어기능과 데이터전달기능이 함께 존재한다. (개별 장비에 파편화)
- SDN은 제어기능과 데이터전달기능을 분리한다.
  - **Control Plane**: 네트워크의 상태를 모니터링하고, 네트워크 장비에게 전달할 명령을 생성 및 관리 (중앙관제)
  - **Data Plane**: 단순 패킷전달만을 수행한다.

### [2] SDN Controller
- Network 장비의 흐름을 관리하는 중앙 집중식 Controller
- OpenFlow와 같은 프로토콜을 통해서 Network 장비를 제어한다.
- Controller를 통해서 Network 경로, 정책등을 소프트웨어로 해결한다.


## 구조
- **Application Layer**: 사용자가 네트워크를 통해서 서비스를 이용할 수 있도록 하는 계층
- **Control Layer**: 네트워크의 상태를 모니터링하고, 네트워크 장비에게 명령을 전달하는 계층
- **Infrastructure Layer**: 네트워크 장비들이 데이터를 전달하는 계층

## 관련 오픈소스
1. OpenFlow
2. OpenDayLight
3. ONOS
4. Ryu