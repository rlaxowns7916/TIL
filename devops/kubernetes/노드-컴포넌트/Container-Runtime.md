# Container Runtime (CRI)

## 개념 (Concept)
- 쿠버네티스가 컨테이너를 실행하기 위해 사용하는 Container Runtime Interface(CRI) 구현체
- Docker, containerd, CRI-O 등 다양한 런타임 지원
- Kubelet이 CRI를 통해 컨테이너 라이프사이클 관리

## 역할 (Roles)
- **컨테이너 실행**: Pod 내 컨테이너 이미지 Pull, 생성, 실행, 중지, 삭제
- **이미지 관리**: 컨테이너 이미지 저장소(Pull, Push, Delete) 관리
- **리소스 격리**: cgroups, namespaces를 통한 리소스 격리
- **네트워킹**: CNI(Container Network Interface)를 통한 네트워크 설정

## 구조 (Architecture)

### CRI (Container Runtime Interface)
- **개념**: 쿠버네티스와 컨테이너 런타임 간의 표준 인터페이스
- **목적**: 런타임 독립성 확보 (Docker, containerd, CRI-O 교체 가능)
- **구성**:
  - **RuntimeService**: 컨테이너 라이프사이클 관리 (Run, Stop, Remove)
  - **ImageService**: 이미지 관리 (Pull, PullImage, ListImages, RemoveImage)

### 주요 Container Runtime
- **containerd (최신 표준)**
  - Docker의 런타임 컴포넌트 분리로 탄생
  - 장점: 가볍고 안정적, Docker 호환
  - 단점: Docker CLI를 별도 설치 필요

- **CRI-O**
  - Red Hat 주도의 OCI(Open Container Initiative) 호환 런타임
  - 장점: 단순, 가볍고 안정적
  - 단점: Docker 호환성 부족

- **Docker Engine (레거시)**
  - 쿠버네티스 초기에 사용
  - 단점: dockershim(deprecated), 무거운 구조
  - 현재: containerd로 마이그레이션 권장

- **gVisor**
  - Google의 보안 컨테이너 런타임
  - 장점: 커널 공유 없이 격리된 환경 제공 (Sandbox)
  - 단점: 성능 저하

## 작동 원리 (How It Works)
1. Kubelet이 API Server로부터 PodSpec 수신
2. Kubelet이 CRI(RuntimeService, ImageService)를 통해 컨테이너 관리 요청 전송
3. Container Runtime이 컨테이너 이미지 Pull (ImageService)
4. Container Runtime이 컨테이너 생성 및 실행 (RuntimeService)
5. cgroups, namespaces를 통한 리소스 격리
6. CNI를 통한 네트워크 설정
7. 컨테이너 상태를 Kubelet에 보고

## 상호작용 (Component Interaction)
- **Kubelet ↔ Container Runtime**: CRI를 통한 gRPC 통신
- **Container Runtime ↔ OCI (Open Container Initiative)**: OCI 표준(runc)을 통한 컨테이너 실행
- **Container Runtime ↔ CNI**: 네트워크 설정 (IP 할당, Routing)
- **Container Runtime ↔ CSI**: 스토리지 볼륨 연결

## OCI (Open Container Initiative)
- **runc**: OCI 표준 컨테이너 런타임 (libcontainer 기반)
- **runtime-spec**: 컨테이너 실행 표준 (config.json)
- **image-spec**: 컨테이너 이미지 표준 (manifest.json)

## 리소스 격리 (Resource Isolation)
- **cgroups**: CPU, Memory, Pid 등 리소스 제한
- **namespaces**: Process, Network, Mount, IPC, UTS, User namespace 격리
- **seccomp**: 시스템 콜 필터링
- **AppArmor/SELinux**: 보안 정책 적용

## 장애 처리 (Failure Handling) ⭐중요
- **Container Runtime 다운 (Container Runtime Down)**
  - 동작 방식: 컨테이너 실행/중지 불가, 기존 컨테이너는 계속 작동
  - 복구 메커니즘: Container Runtime 재시작, Kubelet이 기존 컨테이너 상태 복구

- **이미지 Pull 실패 (Image Pull Failure)**
  - 동작 방식: ImagePullBackOff 상태, 컨테이너 생성 불가
  - 복구 메커니즘: 이미지 태그 확인, 이미지 저장소 접근 권한 확인, 네트워크 확인

- **컨테이너 다운 (Container Crash)**
  - 동작 방식: Kubelet이 컨테이너 다운 감지, RestartPolicy에 따라 재시작
  - 복구 메커니즘: Kubelet이 자동 재시작 (Always/OnFailure), CrashLoopBackOff 시 점차적 지연

- **CRI 장애 (CRI Failure)**
  - 동작 방식: Kubelet이 Container Runtime과 통신 불가, Node 상태 NotReady
  - 복구 메커니즘: Container Runtime 재시작, CRI 소켓 재생성

- **메모리 OOM (Out Of Memory)**
  - 동작 방식: 커널이 메모리 부족 컨테이너 강제 종료 (OOM Killer)
  - 복구 메커니즘: Kubelet이 자동 재시작 (RestartPolicy), Memory Request/Limit 조정

- **디스크 공간 부족 (Disk Full)**
  - 동작 방식: 이미지 Pull 불가, 컨테이너 생성 실패
  - 복구 메커니즘: 디스크 정리 (dangling 이미지 삭제, 불필요한 컨테이너 삭제)

## 사용 사례 (Use Cases)
- containerd를 표준으로 사용하여 가볍고 안정적인 컨테이너 런타임 운영
- gVisor를 사용하여 높은 보안 격리 필요한 워크로드 실행
- Docker에서 containerd로 마이그레이션하여 쿠버네티스 최신 표준 준수

## 참고자료 (References)
- [Container Runtimes - Kubernetes](https://kubernetes.io/docs/setup/production-environment/container-runtimes/)
- [CRI - Kubernetes](https://kubernetes.io/docs/concepts/architecture/#container-runtime-interface)
- [containerd Documentation](https://containerd.io/)
- [CRI-O Documentation](https://github.com/cri-o/cri-o)
