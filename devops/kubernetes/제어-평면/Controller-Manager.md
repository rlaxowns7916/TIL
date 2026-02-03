# Controller Manager (kube-controller-manager)

## 개념 (Concept)
- 쿠버네티스 클러스터 상태(Actual State)를 원하는 상태(Desired State)로 조정하는 상태 조정(Reconciliation) 엔진
- 여러 컨트롤러(Controllers)를 단일 바이너리로 실행하여 관리 효율화
- Watch-Reflect-Reconcile 루프를 통해 상태 동기화

## 역할 (Roles)
- **상태 조정 (Reconciliation)**: 현재 상태와 원하는 상태의 차이를 인지하고 조정
- **리소스 관리**: Node, Pod, Service 등 클러스터 리소스의 수명주기 관리
- **자동 복구 (Self-healing)**: 장애 발생 시 자동으로 복구
- **가비지 컬렉션 (Garbage Collection)**: 더 이상 필요 없는 리소스 삭제

## 구조 (Architecture)

### 주요 컨트롤러 (Key Controllers)
- **Node Controller**: Node 상태 감시, NotReady Node 탐지, Node 존재 여부 확인
- **Replication Controller**: ReplicaSet이 원하는 복제본 수를 유지하도록 관리
- **Endpoints Controller**: Service와 Pod 매칭으로 Endpoints 업데이트
- **Service Account & Token Controller**: Service Account와 API Token 생성/관리
- **Namespace Controller**: Namespace 삭제 시 해당 Namespace 내 리소스 정리
- **Deployment Controller**: Deployment 롤링 업데이트 및 롤백 관리
- **StatefulSet Controller**: StatefulSet의 순차적 생성/삭제/업데이트 관리
- **DaemonSet Controller**: 각 Node에 Pod가 하나씩 실행되도록 관리
- **Job/CronJob Controller**: 일회성 작업(Job)과 주기적 작업(CronJob) 관리
- **PersistentVolume Controller**: PV(볼륨)와 PVC(볼륨 요청) 매칭
- **ResourceQuota Controller**: Namespace별 리소스 사용량 제한 관리
- **HorizontalPodAutoscaler (HPA)**: CPU/Memory 사용량 기반으로 Pod 자동 스케일링

## 작동 원리 (How It Works)
1. **Watch**: API Server에서 etcd의 변경 사항 감시(List/Watch)
2. **Reflect**: 현재 상태(Actual State)를 메모리에 반영
3. **Reconcile**: 원하는 상태(Desired State)와 현재 상태(Actual State)를 비교
4. **Action**: 차이가 있으면 API Server를 통해 조치(생성/삭제/업데이트)

### Reconciliation 루프 예시 (ReplicaSet)
1. ReplicaSet의 desired replicas = 3
2. 현재 실행 중인 Pod 수 = 2
3. 차이 = 1
4. API Server에 Pod 1개 생성 요청 전송

## 상호작용 (Component Interaction)
- **Controller Manager ↔ API Server**: Watch API로 etcd 변경 사항 감시, 조치 요청 전송
- **Controller Manager ↔ etcd**: API Server를 통해 간접적으로 etcd 접근
- **Controller Manager ↔ Scheduler**: ReplicaSet Controller가 스케줄링 필요한 Pod 생성

## Leader Election (리더 선출)
- **개념**: 다중 Controller Manager 구성 시 단일 리더만 활동, 팔로워는 대기
- **과정**:
  1. 각 Controller Manager가 etcd에 리더 후보 등록
  2. etcd Raft 합의를 통해 리더 선출
  3. 리더가 하트비트(Lease) 갱신
  4. 리더 실패 시 팔로워가 새 리더 선출
- **장점**: 고가용성(HA), 분산 시스템에서의 안정성

## 장애 처리 (Failure Handling) ⭐중요
- **Controller Manager 다운 (Controller Manager Down)**
  - 동작 방식: 상태 조정(Reconciliation) 중단, 기존 리소스는 계속 작동, 새로운 작업 미수행
  - 복구 메커니즘: 다중 Controller Manager 구성(HA), Leader Election

- **Node Controller 장애 (Node Controller Failure)**
  - 동작 방식: NotReady Node의 Pod 제거/재스케줄링 불가, Unknown 상태 유지
  - 복구 메커니즘: Controller Manager 재시작, 노드 상태 수동 조정

- **Endpoints Controller 장애 (Endpoints Controller Failure)**
  - 동작 방식: Service의 Endpoints 업데이트 중단, Service 트래픽 라우팅 실패
  - 복구 메커니즘: Controller Manager 재시작, Endpoints 수동 업데이트

- **ReplicaSet Controller 장애 (ReplicaSet Controller Failure)**
  - 동작 방식: 원하는 복제본 수 유지 불가, Pod 다운 시 재생성 불가
  - 복구 메커니즘: Controller Manager 재시작, kubectl scale으로 수동 조정

- **리더 선출 실패 (Leader Election Failure)**
  - 동작 방식: 다중 리더(Split Brain) 발생 가능, 상태 불일치
  - 복구 메커니즘: Controller Manager 재시작, etcd 클러스터 복구

- **리소스 누수 (Resource Leak)**
  - 동작 방식: Garbage Collector 장애 시 삭제된 리소스의 종속 리소스가 정리되지 않음
  - 복구 메커니즘: kubectl delete --cascade 또는 수동으로 종속 리소스 삭제

## 사용 사례 (Use Cases)
- 배포 관리 (Deployment, StatefulSet, DaemonSet)
- 자동 확장 (HorizontalPodAutoscaler)
- 리소스 할당 관리 (ResourceQuota, LimitRange)
- 볼륨 관리 (PersistentVolume Controller)
- 네임스페이스 관리 (Namespace Controller)

## 참고자료 (References)
- [kube-controller-manager - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/components/#kube-controller-manager)
- [Controller Basics - Kubernetes](https://kubernetes.io/docs/concepts/architecture/controller/)
- [Cloud Controller Manager - Kubernetes](https://kubernetes.io/docs/concepts/architecture/cloud-controller/)
- [Leader Election - Kubernetes](https://kubernetes.io/docs/concepts/architecture/control-plane-controller-scheduler/#leader-election)
