# Kubelet

## 개념 (Concept)
- 각 Node에서 실행되는 Agent로 Control Plane과 Node 간의 통신 브릿지
- API Server로부터 PodSpec을 받아 Container Runtime을 통해 컨테이너 실행
- Node 상태를 API Server에 주기적으로 보고(Heartbeat)하고 Self-healing 수행

## 역할 (Roles)
- **PodSpec 실행**: API Server에서 할당된 Pod의 정의(PodSpec)을 실행
- **상태 보고 (Status Reporting)**: Node 상태, Pod 상태, 리소스 사용량을 API Server에 보고
- **자동 복구 (Self-healing)**: 컨테이너 다운 시 자동 재시작
- **리소스 관리 (Resource Management)**: CPU, Memory, Disk 사용량 모니터링
- **cAdvisor 통합**: 컨테이너 메트릭(Metrics) 수집

## 구조 (Architecture)

### 주요 컴포넌트
- **PLEG (Pod Lifecycle Event Generator)**: 컨테이너 라이프사이클 이벤트 감지
- **Sync Loop**: 현재 상태와 원하는 상태(Desired State)를 비교하고 조정
- **cAdvisor**: 컨테이너 리소스 사용량(CPU, Memory, Network, Disk) 수집
- **Eviction Manager**: 리소스 부족 시 Pod 축출(Eviction)

## 작동 원리 (How It Works)
1. API Server로부터 자신에게 할당된 Pod 리스트를 Watch
2. PodSpec을 Container Runtime(CRI)을 통해 컨테이너 생성/실행
3. cAdvisor를 통해 컨테이너 메트릭 수집
4. Node 상태(Ready, NotReady)와 Pod 상태를 API Server에 보고 (Heartbeat: 기본 10초)
5. 컨테이너 다운 시 자동 재시작 (RestartPolicy 설정에 따라)
6. 리소스 부족 시 Pod Eviction

## 상호작용 (Component Interaction)
- **Kubelet ↔ API Server**: HTTPS 통신으로 PodSpec 수신, 상태 보고 (기본 10초 Heartbeat)
- **Kubelet ↔ Container Runtime**: CRI(Container Runtime Interface)를 통해 컨테이너 관리
- **Kubelet ↔ cAdvisor**: 컨테이너 메트릭 수집
- **Kubelet ↔ kube-proxy**: Service Endpoints 동기화

## Pod Lifecycle (Pod 생명주기)
1. **Pending**: 스케줄링 대기
2. **ContainerCreating**: 컨테이너 이미지 Pull, 생성
3. **Running**: 컨테이너 실행 중
4. **Succeeded/Failed**: 컨테이너 완료/실패
5. **Terminating**: 컨테이너 종료 중 (Graceful Shutdown)
6. **Terminated**: 컨테이너 종료 완료

## RestartPolicy (재시작 정책)
- **Always**: 컨테이너 다운 시 항상 재시작 (기본값)
- **OnFailure**: 컨테이너 실패 시에만 재시작 (Exit code 0 제외)
- **Never**: 재시작하지 않음

## 리소스 관리 (Resource Management)
- **Request**: 컨테이너가 보장받을 최소 리소스 (CPU, Memory)
- **Limit**: 컨테이너가 사용할 수 있는 최대 리소스 (CPU, Memory)
- **Eviction (축출)**: 리소스 부족 시 우선순위 낮은 Pod 먼저 축출

## 장애 처리 (Failure Handling) ⭐중요
- **Kubelet 다운 (Kubelet Down)**
  - 동작 방식: Node 상태 NotReady, Heartbeat 손실, Node Controller에 의해 Unknown 상태 변경
  - 복구 메커니즘: Kubelet 재시작, Node Controller가 해당 Node의 Pod를 다른 Node로 재스케줄링

- **컨테이너 다운 (Container Crash)**
  - 동작 방식: Kubelet이 컨테이너 다운 감지, RestartPolicy에 따라 재시작
  - 복구 메커니즘: 자동 재시작 (Always/OnFailure), CrashLoopBackOff 시 점차적 지연

- **리소스 부족 (Resource Exhaustion)**
  - 동작 방식: CPU Throttling, Memory OOM(Out Of Memory)로 컨테이너 강제 종료
  - 복구 메커니즘: Eviction Manager가 저우선순위 Pod 축출, 리소스 Request/Limit 조정

- **이미지 Pull 실패 (Image Pull Failure)**
  - 동작 방식: ImagePullBackOff 상태, 컨테이너 생성 불가
  - 복구 메커니즘: 이미지 태그 확인, 이미지 저장소 접근 권한 확인, 네트워크 확인

- **API Server 연결 실패 (API Server Connection Failure)**
  - 동작 방식: Node 상태 NotReady, 기존 Pod는 계속 작동, 새로운 Pod 미수신
  - 복구 메커니즘: API Server 복구 후 Kubelet 재연결, 상태 재동기화

- **cAdvisor 장애 (cAdvisor Failure)**
  - 동작 방식: 컨테이너 메트릭 수집 불가, Metrics API 응답 불가
  - 복구 메커니즘: Kubelet 재시작, cAdvisor 재시작

## 사용 사례 (Use Cases)
- 클러스터의 각 Node에서 Pod 실행 및 상태 관리
- 리소스 사용량 모니터링 (CPU, Memory, Disk)
- 자동 복구 (컨테이너 다운 시 재시작)
- 노드 상태 보고를 통해 Controller Manager가 클러스터 상태 조정

## 참고자료 (References)
- [kubelet - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/components/#kubelet)
- [Kubelet Node Status - Kubernetes](https://kubernetes.io/docs/concepts/architecture/nodes/#status)
- [Communication between Node and Control Plane - Kubernetes](https://kubernetes.io/docs/concepts/architecture/control-plane-node-communication/)
- [Configure Out of Resource Handling - Kubernetes](https://kubernetes.io/docs/tasks/administer-cluster/out-of-resource/)
