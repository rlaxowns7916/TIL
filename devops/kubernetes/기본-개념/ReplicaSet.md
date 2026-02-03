# ReplicaSet (복제본셋)

## 개념 (Concept)
- 동일한 Pod(Pod Template)의 복제본(Replicas)을 관리하는 컨트롤러
- Deployment가 생성하고 관리하며 직접 사용은 권장되지 않음
- 원하는 복제본 수(Desired Replicas)를 유지하여 가용성 확보

## 역할 (Roles)
- **복제본 유지 (Replica Maintenance)**: 원하는 복제본 수(Replicas) 유지
- **자동 복구 (Self-healing)**: Pod 다운 시 자동으로 새 Pod 생성
- **스케일링 (Scaling)**: 수동/자동으로 복제본 수 조정
- **Pod 생성 (Pod Creation)**: Pod Template을 기반으로 Pod 생성

## 구조 (Architecture)

### ReplicaSet 구성
- **Pod Template**: ReplicaSet이 생성할 Pod의 정의
- **Selector**: ReplicaSet이 관리할 Pod를 식별하는 Label Selector
- **Replicas**: 원하는 복제본 수 (기본: 1)

### Selector (셀렉터)
- **MatchLabels**: Pod의 라벨과 매칭 (단순 매칭)
- **MatchExpressions**: Pod의 라벨과 표현식 매칭 (복잡한 매칭)
  - `In`: 라벨 값이 포함되면 매칭
  - `NotIn`: 라벨 값이 포함되지 않으면 매칭
  - `Exists`: 라벨이 존재하면 매칭
  - `DoesNotExist`: 라벨이 존재하지 않으면 매칭

## 작동 원리 (How It Works)
1. Deployment가 ReplicaSet 생성 (Pod Template, Replicas, Selector)
2. ReplicaSet Controller가 API Server에서 Pod 감시(Watch)
3. ReplicaSet이 Selector에 매칭되는 Pod 탐색
4. 현재 Pod 수(Actual Replicas)와 원하는 Pod 수(Desired Replicas)를 비교
5. 현재 Pod 수가 부족하면 새 Pod 생성 (Pod Template 기반)
6. 현재 Pod 수가 초과하면 Pod 삭제 (Priority 기반: Running > Pending > Terminating)

## 상호작용 (Component Interaction)
- **ReplicaSet ↔ Deployment**: Deployment가 ReplicaSet 생성/관리
- **ReplicaSet ↔ Pod**: ReplicaSet이 Pod 생성/삭제
- **ReplicaSet ↔ Scheduler**: Pod 스케줄링
- **ReplicaSet ↔ Service**: Service가 ReplicaSet의 Endpoints 매칭

## 스케일링 (Scaling)
- **수동 스케일링 (Manual Scaling)**: Replicas 필드 수정
  - `kubectl scale rs <replicaset-name> --replicas=<number>`
- **자동 스케일링 (Auto Scaling)**: HPA(Horizontal Pod Autoscaler)와 연동
  - CPU/Memory 사용량 기반으로 자동 스케일링

## Deployment와 ReplicaSet 관계
- **Deployment → ReplicaSet**: Deployment가 여러 ReplicaSet 생성 가능 (Rolling Update 시)
- **Rolling Update**:
  1. Deployment 업데이트 시 새로운 ReplicaSet 생성 (Replicas: 0)
  2. 새로운 ReplicaSet의 Pod를 점진적으로 생성
  3. 기존 ReplicaSet의 Pod를 점진적으로 삭제
  4. 새로운 ReplicaSet의 Pod를 모두 생성, 기존 ReplicaSet의 Pod를 모두 삭제

## 장애 처리 (Failure Handling) ⭐중요
- **Pod 다운 (Pod Failure)**
  - 동작 방식: ReplicaSet Controller가 Pod 다운 감지, 새 Pod 생성
  - 복구 메커니즘: 자동 복구 (ReplicaSet의 Self-healing)

- **ReplicaSet Controller 다운 (ReplicaSet Controller Failure)**
  - 동작 방식: Pod 관리 중단, 기존 Pod는 계속 작동, 새로운 Pod 미생성
  - 복구 메커니즘: Controller Manager 재시작, ReplicaSet Controller 재시작

- **Selector 매칭 실패 (Selector Matching Failure)**
  - 동작 방식: ReplicaSet이 관리할 Pod를 식별 불가, Pod 미생성
  - 복구 메커니즘: Pod 라벨 확인, ReplicaSet Selector 수정

- **Pod Template 오류 (Pod Template Error)**
  - 동작 방식: Pod 생성 불가, ReplicaSet 상태 Failed
  - 복구 메커니즘: Pod Template 수정, ReplicaSet 재생성

- **스케일링 실패 (Scaling Failure)**
  - 동작 방식: Pod 생성 불가, Desired Replicas 유지 불가
  - 복구 메커니즘: 리소스 확인, Scheduler 상태 확인, Replicas 조정

- **리소스 부족 (Resource Exhaustion)**
  - 동작 방식: Pod 생성 불가, Desired Replicas 유지 불가
  - 복구 메커니즘: 리소스 확장, Replicas 조정, Eviction(우선순위 낮은 Pod 축출)

## 사용 사례 (Use Cases)
- Deployment의 하위 컴포넌트로 사용 (권장)
- 무상태(Stateless) 애플리케이션의 복제본 관리
- 자동 복구(Self-healing) 필요한 애플리케이션
- 수동/자동 스케일링 필요한 애플리케이션

## 참고자료 (References)
- [ReplicaSet - Kubernetes Documentation](https://kubernetes.io/docs/concepts/workloads/controllers/replicaset/)
- [Deployments - Kubernetes Documentation](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)
- [Horizontal Pod Autoscaler - Kubernetes](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)
