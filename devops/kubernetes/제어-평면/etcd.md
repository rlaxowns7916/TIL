# etcd

## 개념 (Concept)
- Go로 작성된 분산형 Key-Value 저장소로 쿠버네티스 클러스터의 신경망(Nervous System)
- 클러스터의 모든 상태 데이터(Cluster State)를 저장하는 단일 진실의 원천(Source of Truth)
- Raft 합의 알고리즘(Consensus Algorithm)을 사용하여 분산 환경에서의 데이터 일관성 보장

## 역할 (Roles)
- **클러스터 상태 저장**: Pod, Service, ConfigMap, Secret 등 모든 Kubernetes 리소스 상태 저장
- **데이터 일관성 보장**: Raft 합의를 통해 쓰기/읽기 일관성 보장
- **리더 선출 (Leader Election)**: 다중 etcd 클러스터에서 리더를 선출하고 장애 시 자동 페일오버
- **Watch API**: 데이터 변경 사항을 실시간으로 Controller에 통지

## 구조 (Architecture)

### Raft 합의 알고리즘
- **Leader (리더)**: 모든 쓰기 요청 처리 및 팔로워와 동기화
- **Follower (팔로워)**: 리더의 요청을 수신하고 로그에 기록, 읽기 전용
- **Candidate (후보)**: 리더 선출 시도 시의 임시 상태

### 토폴로지 (Topology)
- **쿼럼(Quorum)**: 과반수(n/2 + 1)의 동의 필요 (3노드: 2, 5노드: 3)
- **오퍼레이션 모드**: 단일 노드(Dev), 스택형(Stacked, 공유 Master), 외부(External)

## 작동 원리 (How It Works)
1. API Server가 쓰기 요청(Write)을 etcd에 전송
2. 리더가 요청을 로그(Log)에 기록하고 팔로워에게 복제 요청 전송
3. 쿼럼 이상의 팔로워가 응답 시 커밋(Commit)
4. API Server가 읽기 요청(Read) 시 최신 데이터 반환
5. Controller는 Watch API로 etcd 변경 사항 감시

## 상호작용 (Component Interaction)
- **etcd ↔ API Server**: gRPC 통신으로 클러스터 상태 저장/조회
- **etcd ↔ Controller**: Watch API를 통해 etcd의 변경 사항 감시
- **etcd ↔ Scheduler**: 스케줄링된 Node 정보 저장

## 성능 최적화 (Performance Optimization)
- **Batching**: 여러 쓰기 요청을 배치로 처리하여 오버헤드 감소
- **Compression**: etcd v3에서 데이터 압축으로 저장 공간 절약
- **Read-Only Follower**: 읽기 요청을 팔로워에서 분산하여 리더 부하 감소
- **WAL (Write-Ahead Log)**: 데이터 손실 방지를 위한 로그 기록

## 백업 및 복구 (Backup & Recovery)
- **Snapshot**: etcdctl snapshot save로 현재 상태 백업
- **복구**: etcdctl snapshot restore로 백업에서 복구
- **자동 백업**: CronJob을 통한 정기적 스냅샷 생성
- **리더 전역 백업**: EBS 스냅샷 등 스토리지 레벨 백업

## 장애 처리 (Failure Handling) ⭐중요
- **리더 장애 (Leader Failure)**
  - 동작 방식: 팔로워가 리더 하트비트 손실 감지, 후보(Candidate) 상태로 변경 후 리더 선출
  - 복구 메커니즘: 자동 리더 선출(Election), 약 1초 내의 페일오버(Failover)

- **팔로워 장애 (Follower Failure)**
  - 동작 방식: 팔로워 하트비트 손실 시 쿼럼 재계산, 쓰기 요청 처리 계속
  - 복구 메커니즘: 팔로워 재시작 후 로그(Log) 복제 및 동기화

- **쿼럼 손실 (Quorum Loss)**
  - 동작 방식: 과반수 노드 장애 시 쓰기 불가, 클러스터 운영 불가
  - 복구 메커니즘: 노드 재구성, 백업에서 복구 후 새로운 클러스터 구성

- **데이터 일관성 오류 (Data Consistency Error)**
  - 동작 방식: Corrupt 데이터로 인해 API 응답 불가
  - 복구 메커니즘: 최근 스냅샷에서 복구, etcdctl migrate 명령어 사용

- **디스크 공간 부족 (Disk Full)**
  - 동작 방식: 쓰기 실패, API Server 연결 거부
  - 복구 메커니즘: 디스크 확장, etcdctl compact 명령어로 공간 확보

- **네트워크 파티션 (Network Partition)**
  - 동작 방식: 쿼럼 손실 시 쓰기 불가, 두 개의 리더(Split Brain) 방지
  - 복구 메커니즘: 파티션 해제 후 Raft 알고리즘으로 리더 재선출

## 사용 사례 (Use Cases)
- 쿠버네티스 클러스터 상태 저장 (필수)
- Service Discovery 구현 (옵션)
- 분산 설정 관리 (옵션)
- 분산 락(Distributed Lock) 구현 (옵션)

## 참고자료 (References)
- [etcd - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/components/#etcd)
- [etcd Documentation](https://etcd.io/docs/)
- [Operating etcd clusters - Kubernetes](https://kubernetes.io/docs/tasks/administer-cluster/configure-upgrade-etcd/)
- [etcd Admin Guide - Backup & Restore](https://etcd.io/docs/v3.5/op-guide/recovery/)
