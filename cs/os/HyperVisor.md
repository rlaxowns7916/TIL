# HyperVisor
- **하나의 물리적인 Host System에서 다수의 GuestOs(VM)을 동시에 실행시키기 위한 가상화 소프트웨어**
- **여러 H/W 자원을 추상화하고, 여러개의 GuestOS가 물리적 H/W를 공유하면서도 독립적으로 실행 가능하게 한다.**

## 주요기능
1. H/W 자원 분배
   - CPU, Memory, Disk, Network 등의 자원을 VM에 할당

2. VM 격리
   - GuestOS들은 같은 물리적 Host에서 실행되지만, 독립적으로 동작한다.
   - 다른 VM에 접근 할 수 없다.
   - VM끼리의 충돌을 방지하고, 보안성을 강화한다.
3. Snapshot 및 백업
   - VM의 현재 상태를 저장하며, 특정 시점으로 복원이 가능하다.
   - 장애시 빠른 복구가 가능하다.

## 종류
### [1] BareMetal Hypervisor
- H/W에서 직접 실행된다.
- 성능이 뛰어나며, 엔터프라이즈 환경에서 주로 사용
- 물리적 서버에 직접 설치이기 떄문에, 오버헤드가 적고 안정성 및 보안성이 높다.
- 아래와 같은 종류가 있다.
  - VMWare ESXi
  - Microsoft Hyper-V
  - Linux(RedHat) KVM

### [2] Hosted Hypervisor
- Host OS위에서 S/W 형태로 실행되는 Hypervisor
- 일반적인 사용자나 개발자가, Local 환경에서 가상화를 할 떄 주로 사용된다.
- BareMetal에 비해 성능이 떨어지지만, 사용이 간편하다.
- 아래와 같은 종류가 있다.
  - VMWare Workstation/Player
  - Oracle VirtualBox
  - QEMU


## vs Container
- Kernel 공유 여부가 제일 크다.
  - Hypervisor는 각 VM이 별도의 Guest OS를 실행하지만, Container는 Host OS의 커널을 공유한다.
  - Container는 상대적으로 보안에 취약하다.
- 리소스 효율성은 Container가 더 뛰어나다.
  - Hypervisor는 각 VM이 별도의 Guest OS를 실행하기 떄문에, 메모리 및 CPU 사용량이 높다.
  - Container는 Host OS의 커널을 공유하기 떄문에, 가벼운 메모리 및 CPU 사용량을 가진다.

### Host OS의 H/W 접근 방식
- VM는 Hypervisor가 VM에 H/W 자원을 직접 할당한다. 
- Container는 cGroup과 NameSpace를 이용하여 Host OS의 H/W 자원을 접근한다.

| 자원 종류  | VM (Hypervisor 기반) | 컨테이너 (OS 가상화) |
|-----------|-----------------|----------------|
| **CPU**  | Hypervisor가 VM에 CPU 코어를 할당 (vCPU) | 컨테이너는 OS의 프로세스로 실행되어 직접 CPU 사용 |
| **메모리** | VM별로 고정된 메모리를 할당 (메모리 오버헤드 존재) | 컨테이너는 동적으로 메모리를 공유 |
| **디스크** | VM별 가상 디스크(VMDK, QCOW2 등) 사용 | 컨테이너는 Host OS의 파일 시스템을 공유 (OverlayFS 등) |
| **네트워크 (NIC)** | 가상 NIC (vNIC)를 생성하여 Host의 물리 NIC와 연결 | 가상 네트워크 인터페이스 (veth)로 Host의 NIC와 연결 |
| **I/O (디스크, 네트워크)** | Hypervisor가 가상화된 I/O 디바이스를 제공 | Host OS의 네이티브 I/O 성능을 활용 |


### 주요 특징
| 비교 항목         | Hypervisor (VM 기반 가상화) | 컨테이너 (OS 수준 가상화) |
|-----------------|---------------------|------------------|
| **가상화 방식**  | 하드웨어 가상화 (Hypervisor 사용) | 운영체제 가상화 (커널 공유) |
| **운영체제**  | 각 VM이 별도의 Guest OS를 실행 | Host OS의 커널을 공유 |
| **격리 수준** | 강력한 격리 (독립된 OS 실행) | 상대적으로 낮음 (커널 공유) |
| **부팅 속도** | 느림 (OS 부팅 필요) | 빠름 (프로세스 실행 수준) |
| **성능** | 다소 낮음 (하드웨어 가상화 오버헤드) | 높음 (경량화된 실행) |
| **리소스 사용량** | 높은 메모리 및 CPU 사용 | 가벼운 메모리 및 CPU 사용 |
| **보안성** | 높은 보안성 (VM 간 완전 격리) | 낮은 보안성 (커널 공유로 인한 취약점) |
| **스냅샷 및 백업** | VM 단위로 스냅샷 가능 | 컨테이너 이미지로 버전 관리 가능 |
| **마이그레이션** | VM 단위의 마이그레이션 가능 | 컨테이너 단위의 마이그레이션 가능 |
| **사용 예시** | 클라우드 서버, 데이터센터, 보안이 중요한 환경 | 마이크로서비스, DevOps, CI/CD |

