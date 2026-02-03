# Scheduler (kube-scheduler)

## 개념 (Concept)
- 스케줄링되지 않은 Pod(Pending 상태)를 적절한 Node에 할당하는 컴포넌트
- PodSpec에 정의된 리소스 요구사항과 Node 상태를 기반으로 최적의 스케줄링 결정 내림
- 플러그인 기반 아키텍처로 확장 가능 (Scheduling Framework)

## 역할 (Roles)
- **Pod 스케줄링**: Pending 상태의 Pod를 Node에 바인딩(Binding)
- **Predicate 필터링**: Node가 Pod를 실행할 수 있는지 여부 필터링
- **Priority 순위**: 가능한 Node들 중 최적의 Node 선택
- **Preemption (선점)**: 우선순위가 높은 Pod를 위해 기존 Pod 제거 후 스케줄링

## 구조 (Architecture)

### 스케줄링 사이클 (Scheduling Cycle)
1. **Watch**: API Server에서 Pending 상태의 Pod 감시
2. **Filtering (Predicate)**: 가능한 Node 필터링
3. **Scoring (Priority)**: 가능한 Node들 점수 매기기
4. **Binding**: 최고 점수 Node에 Pod 바인딩
5. **Retry**: 스케줄링 실패 시 재시도 (Backoff)

### 스케줄링 프레임워크 (Scheduling Framework)
- **PreFilter**: 스케줄링 전 정보 수집
- **Filter**: Node 필터링 (Pod가 해당 Node에서 실행 가능한지)
- **PreScore**: 점수 계산 전 정보 수집
- **Score**: Node 점수 매기기 (최적 Node 선택)
- **NormalizeScore**: 점수 정규화
- **Reserve**: Node 예약
- **Permit**: 승인 단계 (타임아웃 및 거리 가능)
- **PreBind**: 바인딩 전 작업
- **Bind**: 실제 Node에 Pod 바인딩
- **PostBind**: 바인딩 후 작업

## 작동 원리 (How It Works)
1. API Server에서 Pending 상태의 Pod 감시(Watch)
2. 모든 Node에 대해 Predicate 필터링 실행 (하드 제약사항)
   - **NodeName**: 특정 Node 지정 여부
   - **NodeSelector**: Node 라벨 매칭
   - **NodeAffinity**: Node 선호도/요구사항
   - **Taints/Tolerations**: Node 오염도와 허용도
   - **PodAffinity/Anti-Affinity**: Pod 친화도/반감도
   - **Resources**: CPU, Memory, GPU, Ephemeral Storage 가용 여부
3. 필터링된 Node에 대해 Priority 점수 매기기 (소프트 제약사항)
   - **LeastRequestedPriority**: 리소스 여유가 많은 Node 선호
   - **BalancedResourceAllocation**: CPU/Memory 균형
   - **NodeAffinity**: 선호도 매칭
   - **PodAffinity/Anti-Affinity**: Pod 분배 최적화
4. 최고 점수 Node에 Pod 바인딩
5. 스케줄링 실패 시 재시도 (최대 10회, 지수적 백오프)

## 상호작용 (Component Interaction)
- **Scheduler ↔ API Server**: Watch API로 Pending Pod 감시, Bind 요청 전송
- **Scheduler ↔ Controller**: 스케줄링된 Pod 정보 전달

## 스케줄링 정책 (Scheduling Policies)
- **Node Selector**: 단순 라벨 매칭 (hard constraint)
- **Node Affinity**: 선호도(preferredDuringScheduling)와 요구사항(requiredDuringScheduling)
- **Taints/Tolerations**: Node에 오염도(Taint) 부여, Pod에 허용도(Tolerations) 부여
- **Pod Affinity**: Pod가 같은 Node/Zone/Region에 배치되도록 유도
- **Pod Anti-Affinity**: Pod가 같은 Node에 배치되지 않도록 유도 (분산)
- **PriorityClasses**: Pod 우선순위 지정 (Preemption에 사용)

## Preemption (선점)
- **개념**: 우선순위가 높은 Pod를 실행하기 위해 낮은 우선순위 Pod 제거 후 스케줄링
- **과정**:
  1. 우선순위가 높은 Pending Pod 확인
  2. 해당 Pod를 스케줄링할 수 있는 Node에서 제거 가능한(Preeemptible) Pod 탐색
  3. 제거 후 스케줄링
- **보호**: DaemonSet Pod, PDB(PodDisruptionBudget)로 보호된 Pod는 선점 불가

## 장애 처리 (Failure Handling) ⭐중요
- **스케줄러 다운 (Scheduler Down)**
  - 동작 방식: 새로운 Pod가 스케줄링되지 않고 Pending 상태로 유지
  - 복구 메커니즘: 다중 Scheduler 구성(HA), Leader Election

- **스케줄링 실패 (Scheduling Failure)**
  - 동작 방식: Pod가 Pending 상태로 유지, Event 로그에 실패 원인 기록
  - 복구 메커니즘: Node 추가, 리소스 확장, NodeSelector/Affinity 수정

- **Preemption 실패 (Preemption Failure)**
  - 동작 방식: 우선순위가 높은 Pod가 계속 Pending 상태
  - 복구 메커니즘: 우선순위 낮은 Pod의 PDB 조정, Node 리소스 확장

- **스케줄링 지연 (Scheduling Latency)**
  - 동작 방식: Pod가 Pending 상태로 긴 시간 유지 (수초~수분)
  - 복구 메커니즘: Scheduler 병렬화, 스케줄링 최적화, Node 상태 캐싱

- **NoAvailableNodes (Node 부족)**
  - 동작 방식: 모든 Node가 Predicate 필터링에 실패하여 스케줄링 불가
  - 복구 메커니즘: Cluster Autoscaler로 Node 자동 추가, 리소스 확장

## 사용 사례 (Use Cases)
- 대규모 클러스터에서의 효율적인 리소스 할당
- GPU, SSD 등 특수 하드웨어가 있는 Node에 Pod 배치
- 데이터 지연(Data Locality) 최적화 (PodAffinity 사용)
- 장애 도메인 분산 (Anti-Affinity 사용)

## 참고자료 (References)
- [kube-scheduler - Kubernetes Documentation](https://kubernetes.io/docs/concepts/overview/components/#kube-scheduler)
- [Scheduler Configuration - Kubernetes](https://kubernetes.io/docs/concepts/scheduling/kube-scheduler/)
- [Kubernetes Scheduler Deep Dive - GitHub](https://github.com/kubernetes/community/blob/master/contributors/design-proposals/scheduling/scheduling-framework.md)
- [Pod Priority and Preemption - Kubernetes](https://kubernetes.io/docs/concepts/scheduling-eviction/pod-priority-preemption/)
