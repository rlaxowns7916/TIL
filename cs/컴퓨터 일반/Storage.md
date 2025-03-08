# DAS (Direct Attached Storage)
- Storage를 Server에 직접연결 (HDD, SSD, ...)
- 직접 연결되므로 성능이 빠름.
  - 네트워크를 거치지 않기 때문에 지연이 적음.
- 확장성이 제한적 (특정 서버에 종속됨).
- 여러 서버에서 공유하기 어려움.

# NAS (Network Attached Storage)
- Network(Ethernet (LAN/WAN), Wi-Fi) 를 통해 여러 기기가 공유할 수 있는 파일 저장 시스템.
  - 파일 기반 스토리지로, NFS, SMB/CIFS 등의 프로토콜 사용.
  - 파일단위로 관리된다.
- Server와 Storage 사이에 FileServer가 필요하며, 이것을 통해 Storage를 관리한다.
- 여러 사용자 및 장치가 네트워크를 통해 접근 가능.
- 설정 및 관리가 쉬우며 가정 및 기업 환경에서 많이 사용됨.
- 네트워크를 거쳐야 하므로 속도가 DAS보다 느릴 수 있음.

# SAN (Storage Area Network)
- DAS의 속도 + NAS의 확장성을 모두 가짐
- 전용 Network (TCP/IP 아님) 를 통해 서버와 스토리지를 연결하는 블록 기반 스토리지 시스템.
  - Fibre Channel (FC), iSCSI, FCoE
  - 고속 데이터 전송 가능 (FC 기반일 경우 매우 빠름).
- 블록 레벨 스토리지 제공 → 데이터베이스 및 고성능 애플리케이션에 적합.
- 높은 확장성 및 장애 복구 기능 제공.
- 구축 비용이 높고 관리가 복잡함

### NAS vs SAN
## NAS vs SAN 비교
| 구분              | NAS (Network Attached Storage) | SAN (Storage Area Network) |
|------------------|-----------------------------|---------------------------|
| **네트워크 방식** | 일반적인 TCP/IP 기반 (이더넷) | 전용 스토리지 네트워크 (Fibre Channel, iSCSI) |
| **라우팅 가능 여부** | 가능 (IP 주소 기반) | 불가능 (전용 네트워크) |
| **데이터 접근 방식** | 파일 기반 접근 | 블록 기반 접근 |
| **파일 시스템 관리** | NAS 장치가 직접 관리 | 서버에서 직접 관리 (로컬 디스크처럼 인식) |
| **사용 프로토콜** | SMB, NFS, AFP | Fibre Channel, iSCSI, FCoE |
| **데이터 전송 방식** | TCP/IP를 통한 파일 전송 | 전용 네트워크를 통한 블록 전송 |
| **사용 환경** | 일반적인 파일 공유, 백업 | 고성능 데이터베이스, 가상화 환경 |
| **확장성** | 비교적 쉬움 (네트워크 추가 가능) | 뛰어남 (대규모 확장 가능) |
| **속도** | 네트워크 속도에 따라 제한됨 | Fibre Channel 기반으로 매우 빠름 |
| **비용** | 상대적으로 저렴함 | 고가 (전용 네트워크 및 장비 필요) |


## 저장방식

### [1] Block Storage
- HDD, SSD
- Data를 일정한 Block으로 나누어 저장하는 방식
  - **Block주소를 기반으로 읽고쓴다.**
  - H/W자체는 FileSystem을 이해하지 못하기 때문이다.
- Block들은 각각의 주소를 가지고 있고 논리적으로 연결되어 있으며, 재조합하여 파일을 만들어 낼 수 있다.
- 주로 SAN 또는 VM의 Disk로 사용한다.
- OS나 App에서 Block단위로 데이터를 관리 할 수 있어 고속 I/O에 적합하다.

### [2] FileStorage
- OS가 Block Storage를 FileSysetm으로 변환한다.
  - ext4(Linux), NTFS(Windows), ...
- 일바적인 파일을 계층구조(Tree)로 저장하는 방식
  - 데이터가 늘어날수록 성능이 저하된다.
- 일반적으로 NAS에 사용된다.

### [3] ObjectStorage
- Object라는 개별 데이터 단위로 저장한다.
- 평면(flat) 구조로 데이터를 저장가능하다.
  - 대용량 데이터를 처리하기 위해 주로 사용된다.
  - 데이터를 찾을 때, 고유한 ID와 메타데이터를 활용하게 된다.
- 상세 메타데이터를 커스텀하게 추가 할 수 있다.