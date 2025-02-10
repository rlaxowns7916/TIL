# WSL(Windows Subsystem for Linux)
- Windows에서 직접 Linux 환경을 실행할 수 있도록 해주는 기능
- Microsoft가 Linux의 다양한 기능을 Windows에서 활용할 수 있도록 개발했으며, Windows 10(버전 1607)부터 도입
    - WSL1과 WSL2가 존재한다.

## WSL1 vs WSL2
### WSL1
- Windows 커널이 **Linux 시스템 호출(Syscall)을 변환하여 실행**하는 방식
    - 일부 SystemCall 호출이 제한되었다. (`iptables`, `cgroups` 등 미지원)
    - **Docker 기능이 제대로 호환되지 않았다.** (별도의 Hyper-V VM 필요)
    - HyperVisor 기반 VM을 사용하지 않고 Windows 네이티브 환경에서 동작한다.
- **Linux를 모방한 방식**이기 때문에, Windows와의 **파일 시스템 통합이 빠르다.**
    - 하지만 **WSL1 내부에서 Linux 파일을 다루는 속도는 WSL2보다 느리다.**
    - Windows ↔ Linux 간 파일 공유(`/mnt/c` 경로)는 빠름.

### WSL2
- **Hyper-V 기반 경량 VM**을 통해 실제 **Linux 커널을 실행**하는 방식
- **완전한 Linux SystemCall을 지원**하여 `Docker`, `iptables`, `cgroups` 등의 기능이 동작한다.
- **Linux 파일 시스템(ext4 기반 VHD)** 을 사용하여 파일 접근 속도가 향상되었다.
    - 하지만 Windows 파일 시스템(`/mnt/c`)과의 공유 속도는 느려진다.
    - 이는 Windows가 WSL2의 파일 시스템을 네트워크 드라이브 방식으로 접근하기 때문.

---

## WSL1 vs WSL2 비교

| 항목 | WSL1 | WSL2 |
|------|------|------|
| **아키텍처** | Windows 커널에서 Linux 시스템 호출을 변환하여 실행 | Hyper-V 기반 경량 VM에서 실제 Linux 커널 실행 |
| **파일 시스템 성능** | Windows 파일 시스템(`/mnt/c`)에서 빠름 | Linux 내부 파일 시스템(ext4)에서 빠름 |
| **호환성** | 일부 SystemCall 미지원 (`iptables`, `cgroups` 사용 불가) | 완전한 Linux 커널 지원 (`Docker`, `iptables`, `cgroups` 사용 가능) |
| **네트워크** | Windows와 같은 네트워크 스택 사용 | 별도 가상 네트워크 사용 (WSL2에 별도 IP 할당) |
| **시스템 리소스 사용** | 가벼움 (VM 없이 직접 실행) | Hyper-V 기반 경량 VM 사용 (메모리 사용 증가 가능) |
| **Docker 지원** | 기본적으로 미지원 (별도 VM 필요) | 기본 지원 (네이티브 Docker 실행 가능) |
| **부팅 속도** | 매우 빠름 (바로 실행됨) | 상대적으로 느림 (VM을 부팅해야 함) |
| **Windows ↔ Linux 파일 접근** | `/mnt/c` 경로 접근 속도가 빠름 | `/mnt/c` 경로 접근 속도가 느림 (반대로, Linux 파일 시스템 내 I/O 속도는 빠름) |
| **커널 업데이트** | Windows 업데이트와 무관 | Windows Update를 통해 자동 커널 업데이트 |
| **사용 용도** | Windows와의 파일 공유가 중요한 작업 | Linux 환경과의 호환성이 중요한 작업 |