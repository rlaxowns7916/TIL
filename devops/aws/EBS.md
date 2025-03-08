# EBS (Elastic Block Store)
- EC2 인스턴스가 접근가능한 Block Storage이며 Network를 통해 접근한다. (물리적인 연결 X)
  - HDD나 SSD처럼 동작한다.
    - 데이터를 영구적으로 저장 가능하다. (Ec2 Instance Store와 반대 / 성능은 Ec2 Instance Store가 더 뛰어나다 - 실제 Ec2에 연결된 물리장비의 Storage를 사용하는 것이기 때문)
  - 동적으로 크기 조정이 가능하다. (확장은 가능하나 축소는 불가능하다.)
    - 더 작은 볼륨을 사용하려면, EBS 볼륨을 새로만들고 마이그레이션 해야한다.
- **기본적으로는 EC2 Instance 하나에만 Attach 될 수 있다.** (하나의 Ec2는 여러개의 EBS를 가질 수는 있다.)
  - **io1, io2 같은 고성능 볼륨은 Multi-Attach 기능을 통해서 여러 Instance에 공유가 가능하다.** 
    - **하나의 Volume에 최대 16개의 Ec2 인스턴스가 부착 가능하다.**
  - EC2종료시 삭제되는 옵션도 있다. 
    - RootVolume은 삭제가 Default
    - 다른 EBSVolume은 삭제가 Default가 아니다.
- **AZ에 국한된다. (다른 AZ 연결 불가능)**
  - 다른 AZ로 이동시키려면 snapshot을 이동시켜야한다.

## 사용사례
- RDS를 사용하지않고 DB를 직접 운영하는 경우
- 웹서버에서 동적으로 파일을 생성할 경우 (이미지 프로세싱,로그, ...)
- CI/CD 빌드 캐시

## Volume Type
- gp2/gp3: 일반적인 SSD
  - EC2 boot volume에 사용가능(OS)
- io1/io2 express: 고성능 SSD이며, 저지연 고처리량 작업에 사용한다.
  - EC2 boot volume에 사용가능(OS)
- st1: 저비용 대용량에 적합한 HDD이며 자주 Access하며 처리량이 많을 때 사용한다. 
  - 빅데이터 처리, LogStorage, Streaming데이터 저장소
- sc1: 제일 저렴한 HDD이며 Access 빈도가 적은 작업에서 사용한다.  
  - BackUp, Archive, 오래된 Log Storage

### Volume 타입 별 IOPS

| **EBS 타입**  | **최대 IOPS** | **설명** |
|--------------|------------|---------|
| **gp2 (General Purpose SSD, 기존)** | **최대 16,000 IOPS** | 볼륨 크기(GB) × 3 만큼 IOPS 증가, 최대 16,000 IOPS 제한. |
| **gp3 (General Purpose SSD, 최신)** | **최대 16,000 IOPS** | 볼륨 크기와 관계없이 IOPS를 개별적으로 설정 가능. |
| **io1 (Provisioned IOPS SSD)** | **최대 64,000 IOPS** | 고성능 SSD, IOPS를 직접 설정 가능. |
| **io2 (Provisioned IOPS SSD, 최신)** | **최대 256,000 IOPS** | io1보다 높은 내구성을 제공하며, RAID 0을 활용해 성능 확장 가능. |
| **st1 (Throughput Optimized HDD)** | **최대 500 IOPS** | 순차적인 읽기/쓰기 작업에 최적화된 HDD, 일반적인 랜덤 접근에는 적합하지 않음. |
| **sc1 (Cold HDD, 저가형)** | **최대 250 IOPS** | 가장 저렴한 옵션, 장기 데이터 보관용. |

## 🔹 **참고 사항**
- **gp2는 볼륨 크기를 늘리면 IOPS 증가하지만, 16,000 IOPS 이상은 불가능.**
- **gp3는 볼륨 크기와 상관없이 원하는 IOPS를 설정할 수 있음.**
- **고성능이 필요한 경우 io1/io2를 선택하고, RAID 0을 활용하면 310,000 IOPS 이상도 가능.**
- **HDD 타입(st1, sc1)은 순차적 데이터 접근용이며, 높은 IOPS가 필요한 경우 적절하지 않음.**

## Snapshot
- EBS의 특정시점에 대한 Snapshot이다.
  - 어플리케이션이 많은 트래픽을 받고있는 시점에서 수행하며 안된다.
- 이 Snapshot을 통해서 다른 Region에 사용 가능하다.
- Snapshot에도 비용이든다.

### Snapshot Archive
- 75% 더 싸게 Archive Tier로 Snapshot을 옮긴다.
- 복구하는데 시간이 더 걸린다. (24~72 시간)

### Recycle Bin for Ebs
- EBS 스냅샷을 영구적으로 삭제하는 대신 휴지통에 보관한다.
- 실수로 삭제했을 때 빠르게 복구하기 위함이다.
- 보관기간을 정할 수 있다. (from ~ 1year)
 
### Fast Snapshot Restore (FSR)
- Snapshot이 매우 클 때, EBS볼륨을 초기화해야하거나 빠르게 인스턴스화 해야할 때 유용하다.
- 비용이 많이든다.