# Pod (파드)

## 개념 (Concept)
- 쿠버네티스에서 실행되는 최소 배포 단위(Deployment Unit)
- 하나 이상의 컨테이너(Containers)를 포함하며 같은 Node에서 실행됨
- 컨테이너 간 스토리지, 네트워크, 구성 공유

## 역할 (Roles)
- **컨테이너 실행**: 하나 이상의 컨테이너(Containers) 실행
- **자원 공유**: 컨테이너 간 스토리지(Volume), 네트워크, 구성 공유
- **라이프사이클 관리**: 컨테이너 생성, 실행, 종료, 재시작
- **스케줄링 단위**: 하나의 Node에 할당되는 최소 단위

## 구조 (Architecture)

### Pod 구성
- **Pause 컨테이너**: Pod의 네트워크 및 스토리지 네임스페이스 초기화, 컨테이너간 통신 중계
- **애플리케이션 컨테이너**: 실제 애플리케이션 실행 컨테이너 (주 컨테이너)
- **사이드카 컨테이너**: 주 컨테이너 보조 역할 (로그 수집, 프록시, 모니터링)
- **Init 컨테이너**: 메인 컨테이너 시작 전 초기화 작업 수행

### 컨테이너 패턴 (Container Patterns)
- **Sidecar Pattern**: 로그 수집, 프록시, 모니터링 컨테이너 추가
- **Ambassador Pattern**: 프록시 컨테이너를 통한 외부 서비스 접근
- **Adapter Pattern**: 애플리케이션 출력을 표준 형식으로 변환

## 작동 원리 (How It Works)
1. 사용자가 Pod YAML/JSON으로 정의 (PodSpec)
2. API Server에 Pod 생성 요청 전송
3. Scheduler가 Pod를 적절한 Node에 할당
4. Kubelet이 Container Runtime을 통해 컨테이너 실행
5. Pause 컨테이너가 네임스페이스 생성, 다른 컨테이너가 참조
6. Init 컨테이너가 순차적으로 실행
7. 메인 컨테이너 실행, 사이드카 컨테이너 병행 실행

## 상호작용 (Component Interaction)
- **Pod ↔ Scheduler**: 스케줄링 결정 (Node 할당)
- **Pod ↔ Kubelet**: 컨테이너 실행, 상태 보고
- **Pod ↔ Container Runtime**: 컨테이너 생성, 실행, 중지
- **Pod ↔ Volume**: 스토리지(Ephemeral, Persistent) 연결
- **Pod ↔ Service**: Service를 통한 네트워킹 및 로드 밸런싱

## Pod Lifecycle (Pod 생명주기)
1. **Pending**: 스케줄링 대기, 이미지 Pull 중
2. **ContainerCreating**: 컨테이너 생성 중
3. **Running**: 최소 하나의 컨테이너 실행 중
4. **Succeeded**: 모든 컨테이너 성공 종료 (Job 등)
5. **Failed**: 하나 이상의 컨테이너 실패
6. **Unknown**: Node 통신 실패로 상태 불확실

## RestartPolicy (재시작 정책)
- **Always**: 컨테이너 다운 시 항상 재시작 (기본값)
- **OnFailure**: 컨테이너 실패 시에만 재시작 (Exit code 0 제외)
- **Never**: 재시작하지 않음

## 장애 처리 (Failure Handling) ⭐중요
- **Pod 다운 (Pod Failure)**
  - 동작 방식: Pod의 모든 컨테이너 다운, Pod 상태 Failed
  - 복구 메커니즘: ReplicaSet/Deployment Controller가 새 Pod 생성

- **컨테이너 다운 (Container Crash)**
  - 동작 방식: Kubelet이 컨테이너 다운 감지, RestartPolicy에 따라 재시작
  - 복구 메커니즘: 자동 재시작 (Always/OnFailure), CrashLoopBackOff 시 점차적 지연

- **Init 컨테이너 실패 (Init Container Failure)**
  - 동작 방식: 메인 컨테이너 시작 불가, Pod 상태 Failed
  - 복구 메커니즘: Init 컨테이너 로그 확인, ConfigMap/Secret 확인, 재시작

- **이미지 Pull 실패 (Image Pull Failure)**
  - 동작 방식: ImagePullBackOff 상태, 컨테이너 생성 불가
  - 복구 메커니즘: 이미지 태그 확인, 이미지 저장소 접근 권한 확인, ImagePullSecrets 확인

- **리소스 부족 (Resource Exhaustion)**
  - 동작 방식: CPU Throttling, Memory OOM(Out Of Memory)로 컨테이너 강제 종료
  - 복구 메커니즘: Kubelet이 자동 재시작 (RestartPolicy), Memory Request/Limit 조정

- **LivenessProbe 실패 (LivenessProbe Failure)**
  - 동작 방식: 컨테이너가 응답하지 않는 것으로 판단, 컨테이너 재시작
  - 복구 메커니즘: 자동 재시작, Probe 설정(Period, Timeout) 조정

- **ReadinessProbe 실패 (ReadinessProbe Failure)**
  - 동작 방식: 컨테이너가 트래픽을 받을 준비 안 된 것으로 판단, Service Endpoints에서 제외
  - 복구 메커니즘: 자동 복구, Endpoints 재동기화

- **Node 다운 (Node Failure)**
  - 동작 방식: Node 상태 NotReady, 해당 Node의 Pod 접근 불가
  - 복구 메커니즘: Node Controller가 Pod를 다른 Node로 재스케줄링

## 사용 사례 (Use Cases)
- 단일 컨테이너 실행 (Web Server, API)
- 멀티 컨테이너 패턴 (Web + Log Collector, Web + Proxy)
- 배치 작업 (Job, CronJob)
- StatefulSet (Database, Message Queue)

## 참고자료 (References)
- [Pods - Kubernetes Documentation](https://kubernetes.io/docs/concepts/workloads/pods/)
- [Pod Lifecycle - Kubernetes](https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/)
- [Configure Liveness and Readiness Probes - Kubernetes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)
- [Init Containers - Kubernetes](https://kubernetes.io/docs/concepts/workloads/pods/init-containers/)
