# Deployment

## 개념 (Concept)
- ReplicaSet(복제본)을 관리하여 애플리케이션 배포 및 업데이트를 선언적으로 관리
- Rolling Update, Rollback, Pause/Resume 등 배포 전략 지원
- 무상태(Stateless) 애플리케이션에 적합

## 역할 (Roles)
- **배포 관리 (Deployment Management)**: 애플리케이션 배포, 업데이트, 롤백
- **복제본 유지 (Replica Maintenance)**: 원하는 복제본 수(Replicas) 유지
- **롤링 업데이트 (Rolling Update)**: 서비스 중단 없는 업데이트
- **롤백 (Rollback)**: 이전 버전으로의 빠른 복구

## 구조 (Architecture)

### Deployment 구성
- **ReplicaSet**: Deployment가 관리하는 복제본 집합 (하나의 Deployment는 여러 ReplicaSet 소유 가능)
- **Pod Template**: ReplicaSet이 생성할 Pod의 정의
- **Deployment Strategy**: 업데이트 전략 (RollingUpdate, Recreate)

### 업데이트 전략 (Deployment Strategy)
- **RollingUpdate (기본)**: 점진적 업데이트로 서비스 중단 없음
  - `maxUnavailable`: 업데이트 중 최대 다운 가능한 Pod 비율 (기본 25%)
  - `maxSurge`: 업데이트 중 최대 추가 생성 가능한 Pod 비율 (기본 25%)

- **Recreate**: 기존 Pod 전체 삭제 후 새로운 Pod 생성
  - 장점: 단순
  - 단점: 서비스 중단 발생

## 작동 원리 (How It Works)
1. 사용자가 Deployment YAML/JSON으로 정의 (Pod Template, Replicas)
2. API Server에 Deployment 생성 요청 전송
3. Deployment Controller가 ReplicaSet 생성
4. ReplicaSet이 원하는 복제본 수(Replicas)만큼 Pod 생성
5. Scheduler가 Pod를 적절한 Node에 할당
6. Deployment 업데이트 시 새로운 ReplicaSet 생성
7. Rolling Update: 새로운 ReplicaSet의 Pod를 점진적으로 생성, 기존 ReplicaSet의 Pod를 점진적으로 삭제
8. Rollback: 이전 ReplicaSet으로 롤백 (새 ReplicaSet 삭제, 이전 ReplicaSet 활성화)

## 상호작용 (Component Interaction)
- **Deployment ↔ ReplicaSet**: Deployment가 ReplicaSet 생성/관리
- **ReplicaSet ↔ Pod**: ReplicaSet이 Pod 생성/삭제
- **Deployment ↔ Scheduler**: Pod 스케줄링
- **Deployment ↔ Service**: Service가 ReplicaSet의 Endpoints 매칭

## 배포 라이프사이클 (Deployment Lifecycle)
1. **Progressing**: 배포 진행 중 (Rolling Update)
2. **ReplicaSet Updated**: 새로운 ReplicaSet 생성
3. **Pods Created**: 새로운 Pod 생성
4. **Pods Ready**: 새로운 Pod가 트래픽을 받을 준비 완료
5. **Complete**: 배포 완료
6. **Rollback**: 롤백 실행 (필요 시)

## Rolling Update 과정 (Rolling Update)
1. Deployment 업데이트 시 새로운 ReplicaSet 생성 (Replicas: 0)
2. 새로운 ReplicaSet의 Pod를 `maxSurge`만큼 생성
3. 기존 ReplicaSet의 Pod를 `maxUnavailable`만큼 삭제
4. 과정 2-3 반복하여 새로운 ReplicaSet의 Pod를 모두 생성
5. 기존 ReplicaSet의 Pod를 모두 삭제

## Rollback (롤백)
- **개념**: 이전 버전으로 복구
- **과정**:
  1. Deployment 롤백 명령 전송
  2. 현재 ReplicaSet 보존 (이전 버전)
  3. 이전 ReplicaSet(롤백 버전) 활성화
  4. 현재 ReplicaSet의 Pod 삭제, 이전 ReplicaSet의 Pod 생성

## Deployment Status (Deployment 상태)
- **Progressing**: 배포 진행 중
- **Complete**: 배포 완료
- **Failed**: 배포 실패
- **Paused**: 배포 일시 중지

## 장애 처리 (Failure Handling) ⭐중요
- **Pod 다운 (Pod Failure)**
  - 동작 방식: ReplicaSet이 새 Pod 생성, 원하는 복제본 수 유지
  - 복구 메커니즘: ReplicaSet의 자동 복구

- **배포 실패 (Deployment Failure)**
  - 동작 방식: Deployment 상태 Failed, 이전 버전 유지
  - 복구 메커니즘: 롤백(kubectl rollout undo), 배포 재시도

- **Rolling Update 중단 (Rolling Update Stuck)**
  - 동작 방식: `maxSurge`/`maxUnavailable` 설정으로 인해 업데이트 중단
  - 복구 메커니즘: 배포 롤백 또는 `maxSurge`/`maxUnavailable` 조정 후 재시도

- **이미지 Pull 실패 (Image Pull Failure)**
  - 동작 방식: Pod 생성 불가, ImagePullBackOff 상태
  - 복구 메커니즘: 이미지 태그 확인, 이미지 저장소 접근 권한 확인, 롤백

- **LivenessProbe/ReadinessProbe 실패 (Probe Failure)**
  - 동작 방식: 컨테이너 다운 감지, 재시작, 업데이트 중단
  - 복구 메커니즘: Probe 설정(Period, Timeout) 조정, 롤백

- **Deployment Controller 다운 (Deployment Controller Failure)**
  - 동작 방식: ReplicaSet/Pod 관리 중단, 기존 Pod는 계속 작동
  - 복구 메커니즘: Controller Manager 재시작, Deployment Controller 재시작

- **롤백 실패 (Rollback Failure)**
  - 동작 방식: 이전 버전의 ReplicaSet 없음으로 롤백 불가
  - 복구 메커니즘: 이전 버전의 Deployment YAML로 재배포

## 사용 사례 (Use Cases)
- 웹 애플리케이션 배포 (Rolling Update)
- API 서버 배포 및 업데이트
- CI/CD 파이프라인 통합 (자동 배포)
- 롤백 필요한 애플리케이션 (버그 발생 시)

## 참고자료 (References)
- [Deployments - Kubernetes Documentation](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)
- [Update a Deployment - Kubernetes](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#updating-a-deployment)
- [Rolling Back a Deployment - Kubernetes](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#rolling-back-a-deployment)
- [Deployment Status - Kubernetes](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#deployment-status)
