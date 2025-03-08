# EC2 (Elastic Compute Cloud)
- IaaS
- VM을 대여 한다.
- 아래를 설정 가능하다.
  - OS: Linux, Mac, Windows
  - CPU
  - RAM
  - NetworkCard: speed & Public IP
  - Firewall: SecurityGroup
- KeyPair를 통해서 접근하는 것이 권장된다. (SSH 이용 시)
  - .pem: Mac, Linux, Windows10
  - .ppk: Windows10 미만버전 (& Putty)
- **Instance 재시작 시 Public IP는 변경될 수 있다. (Private IP는 변경되지 않는다.)**

# Bootstrap
- EC2인스턴스가 처음 시작될 때 실행되는 초기화 스크립트 (설정)
  - ShellScript 또는 Cloud-Init을 실행한다.
- **UserData를 활용하여 설정하며, 부팅될 때 필요한 설정 (소프트웨어 설치, 업데이트, ...)을 자동으로 적용한다.**
  - EC2는 UserData를 RootUser로 인식한다. (sudo 권한 수행)

## 명명 규칙
```text
m5.2xlarge
```
- m: instance class
- 5: generation
- 2xlarge: size of the instance class

### General Purpose (범용 인스턴스)
- WebServer 등 다양한 목적에 적합하다.
- Compute, Memory, Network 가 고르게 설정되어있다.
- T4g, T3, T3a, T2, M6g, M5, ...

### Compute Optimized (컴퓨팅 최적화)
- 고성능 프로세서를 활용한 컴퓨팅 집약적인 모델에 적합
  - 미디어 트랜스코딩, 게임 서버 및 광공ㄴ진, 기계학습, ...
- c8g.medium, c8g.large, ...

### Memory Optimized (메모리 최적화)
- 큰 Memory용량이 필요한 서버에 사용된다.
  - 고성능 RDB, NoSQL 데이터베이스
  - 분산 스토리지
- R6g,R5,...

### Storage Optimized (스토리지 최적화)
- Storage-Intensive한 Task에 적합하며, 대용량 Read/Write를 수행하는 업무에 적합하다.
- OLTP, OLAP, 로그저장, ...
- I3, I4, D2, ...

## 종류
### [1] On-Demand Instance
- 사용한 만큼 지불하는 방식
- 시간단위 (보통 sec)로 요금부과
- 예측불가능한 워크로드, 단기 테스트 환경에 적합하다.
- **초기 비용이 없으며, 필요할 때마다 생성 가능하다**

### [2] Reserved Instance
- 1년, 3년 단위로 특정 Instance를 예약하여 비용을 절감할 수 있다.
- 선결제, 혹은 월 정액방식으로 지불한다.
- 장기적으로 예측가능한 워크로드에 적합하다.
- **On-Demand에 비해서 최대 75%절약이 가능하며, 정해진 용량을 보장하지만 Instance유형과 Region에 제한된다.**

### [3] Spot Instance
- AWS의 유휴(idle) 자원을 이용하는 방식으로, On-Demand보다 최대 90% 저렴하다.
  - 시장가격에 따라 변동되는 방식이다.
  - 저렴한 비용으로 대용량의 Instance 사용이 가능하다.
- **대규모 배치작업, 빅데이터분석, CI/CD 파이프라인 등에 적합하다**


### [4] Dedicated Host
- **특정 물리서버를 단독으로 사용하는 방식**
- Host단위로 요금이 부과된다.
- **규제 및보안 요구사항 및 라이센스 제한적인 환경에 속한 어플리케이션(Windows Server, Oracle DB, ...)에 적합하다.
- **H/W를 완전히 전용으로사용하고, S/W 라이센스 비용 최적화가 가능하나, 비용이 높다.**

### [5] Dedicated Instance
- 단일 AWS 계정 내에서 물리적인 서버를 공유하지 않는 방식
  -  다른 AWS 계정의 인스턴스와 같은 물리적 서버에서 실행되지 않도록 보장한다. (같은 계정내에서 공유)
  - 다른 Dedicated Instance와는 같이 실행될 수 있다.
- 규제가 요구하는 물리적 격리에 사용 가능하다. (금융, 헬스케어, ...)
- Instnace비용 + 물리서버당 추가비용이 발생한다.
  - 다른 AWS계정과 사용이 불가능하게 구성이되어있기 때문에, 어찌되었든 물리서버를 독점하는 것이기 때문이다.


## AMI (Amazon Machine Image)
EC2 인스턴스를 생성할 때 사용하는 이미지.

### AMI의 종류
- **공용 AMI**: AWS에서 기본 제공하는 이미지 (Amazon Linux, Ubuntu, Windows Server 등)
- **마켓플레이스 AMI**: AWS Marketplace에서 제공하는 상용 AMI (Red Hat, SUSE, Windows Server 등)
- **커스텀 AMI**: 사용자가 직접 만든 AMI (보안 패치, 애플리케이션 포함 가능)


### AMI의 구성 요소
| 구성 요소 | 설명 |
|----------|------|
| **Root Volume** | OS 및 소프트웨어 설정을 포함하는 기본 볼륨 |
| **Launch Permissions** | 특정 AWS 계정과 AMI 공유 가능 |
| **Block Device Mappings** | AMI에 포함될 EBS 볼륨 설정 |
| **Virtualization Type** | HVM 또는 PV (현재는 HVM이 주로 사용됨) |


###  AMI의 주요 활용 사례
1. **동일한 환경의 EC2 인스턴스를 빠르게 배포**
2. **OS 및 애플리케이션을 사전 설치하여 운영 효율성 향상**
3. **백업 및 복원 용도로 활용 (스냅샷 기반)**
4. **다른 리전에 동일한 환경을 배포할 때 유용**

