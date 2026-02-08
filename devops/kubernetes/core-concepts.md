# Kubernetes 핵심 개념 (Core Concepts)

## 1. 개요
Kubernetes(K8s)는 컨테이너화된 애플리케이션의 배포, 확장, 관리를 자동화하는 오픈소스 시스템입니다. 이 문서는 K8s의 가장 기초가 되는 **클러스터 아키텍처, 노드(Node), 파드(Pod)**의 개념을 구조적으로 정리합니다.

## 2. 클러스터 아키텍처 (Cluster Architecture)

Kubernetes 클러스터는 크게 **Control Plane(제어 평면)**과 **Worker Node(워커 노드)**로 구성됩니다.

### 2.1. Control Plane (마스터 노드)
클러스터 전체의 상태를 관리하고 제어하는 두뇌 역할을 합니다.

- **kube-apiserver:**
  - 모든 요청의 진입점(Gateway)입니다.
  - REST API를 제공하며, 인증/인가 및 요청 유효성 검사를 수행합니다.
- **etcd:**
  - 클러스터의 모든 데이터를 저장하는 고가용성 Key-Value 저장소입니다.
  - 유일하게 상태(State)를 저장하는 컴포넌트입니다.
- **kube-scheduler:**
  - 새로 생성된 Pod를 감지하고, 리소스 상태를 고려하여 적절한 노드에 배치(Scheduling)합니다.
- **kube-controller-manager:**
  - 노드 컨트롤러, 레플리카셋 컨트롤러 등 다양한 컨트롤러 프로세스를 실행하고 상태를 유지합니다.

### 2.2. Worker Node (데이터 평면)
실제 애플리케이션(컨테이너)이 실행되는 환경입니다.

- **kubelet:**
  - 각 노드에서 실행되는 에이전트입니다.
  - Control Plane의 명령을 받아 컨테이너를 실행하고 상태를 보고합니다.
- **kube-proxy:**
  - 노드의 네트워크 규칙을 유지 관리합니다.
  - 서비스(Service) 개념을 구현하여 트래픽을 파드로 전달합니다.
- **Container Runtime:**
  - 실제로 컨테이너를 실행하는 소프트웨어입니다 (예: Docker, containerd, CRI-O).

---

## 3. 노드 (Node)

노드는 클러스터의 워커 머신(VM 또는 물리 머신)입니다.

### 핵심 특징
- **컴퓨팅 리소스:** CPU, Memory를 제공하여 Pod를 실행합니다.
- **상태 관리:** Ready, NotReady 등의 상태를 가지며, Control Plane이 이를 지속적으로 모니터링합니다.
- **용량(Capacity) 및 할당 가능량(Allocatable):**
  - 전체 리소스 중 시스템 데몬(OS, Kubelet 등)을 제외한 리소스만 Pod에 할당 가능합니다.

---

## 4. 파드 (Pod)

Kubernetes에서 생성하고 관리할 수 있는 **가장 작은 배포 단위**입니다.

### 4.1. 개념
- 하나의 Pod는 **하나 이상의 컨테이너 그룹**입니다.
- Docker 컨테이너와 달리, K8s는 컨테이너를 직접 관리하지 않고 Pod 단위로 관리합니다.

### 4.2. 특징
- **IP 공유:** Pod 내의 모든 컨테이너는 하나의 IP를 공유합니다 (localhost로 통신 가능).
- **스토리지 공유:** Volume을 마운트하여 컨테이너 간 파일을 공유할 수 있습니다.
- **일시성 (Ephemeral):** Pod는 영구적이지 않습니다. 죽으면 재생성되며, 이때 IP가 변경될 수 있습니다.

### 4.3. 디자인 패턴
- **Sidecar 패턴:** 메인 애플리케이션 컨테이너 + 보조 기능을 하는 컨테이너(로그 수집, 프록시 등)를 함께 배치.

---

## 5. 요약: 전체 흐름

1. **사용자**가 `kubectl`로 Pod 생성 요청을 보냄.
2. **API Server**가 요청을 받아 **etcd**에 기록.
3. **Scheduler**가 새 Pod를 발견하고, 적절한 **Node**를 선정.
4. 해당 Node의 **kubelet**이 지시를 받아 **Container Runtime**을 통해 컨테이너 실행.
5. **kube-proxy**가 네트워크 통신을 지원.

이 구조를 통해 Kubernetes는 선언적(Declarative) API를 기반으로 "바라는 상태(Desired State)"를 지속적으로 유지합니다.
