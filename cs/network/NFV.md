# NFV (Network Functions Virtualization)
- 전통적으로 H/W 기반으로 제공되던 Network 기능을 가상화 하여 S/W로 대체하는 기술
- Server에서 가상화된 방식으로 Network 기능을 제공한다.
- 아래와 같은 장점이 있다.
  - Network 기능을 손쉽게 배포가능 (유연성 증가)
  - H/W 종속성 감소
  - Cloud환경에 대한 높은 이식성

## 핵심개념

### [1] VNF (Virtualized Network Function)
- Network 장비의 기능을 가상화한 S/W
- ex) Firewall, LoadBalancer, Router, Switch, ...

### [2] NFVI (Network Functions Virtualization Infrastructure)
- VNF가 동작하는 환경
- H/W자원 (Server, Storage, Network)와 가상화 기술(Hypervisor, Container)등을 포함한다.

### [3] NFV MANO(MANagement and Orchestration)
- NFV의 전체적인 운영 및 자동화를 담당하는 관리시스템 (VNF, NFVI를 관리)
- VNF를 배포, 리소스할당 및 최적화, 상태 모니터링등을 수행