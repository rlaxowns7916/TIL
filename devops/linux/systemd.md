# systemd
- Linux 시스템의 부팅시 초기화 시스템(init system) 이자 서비스 관리자
- service, socket, timer, mount, point, ... 등 다양한 시스템 리소스를 관리
- 주요 구성 파일이 존재한다.
  - .service, 
  - .timer, 
  - .socket 
  - ...
- timer파일을 통해 service파일의 실행을 정의할 수 있다.
- 

## service 파일
```shell
[Unit]
Description=시스템 서비스 설명
After=network.target

[Service]
Type=simple/oneshot/forking 등
ExecStart=/경로/실행파일
WorkingDirectory=/작업/디렉토리
Restart=on-failure/always/no 등
RestartSec=5

[Install]
WantedBy=multi-user.target
```
- type
  - simple: 서비스가 단일 프로세스인 경우
  - forking: 서비스가 fork()를 호출하여 자식 프로세스를 생성하는 경우
  - oneshot: 서비스가 단일 작업을 수행하고 종료되는 경우

## timer 파일
```shell
[Unit]
Description=타이머 설명
Requires=my-service.service

[Timer]
OnBootSec=5min
OnUnitActiveSec=1h
Unit=my-service.service

[Install]
WantedBy=timers.target
```
- unit- timer가 실행할 unit을 지정
- OnBootSec- 부팅 후 지정된 시간에 타이머가 시작
- OnUnitActiveSec- 지정된 시간 간격으로 타이머가 활성화


## systemctl 명령어
- systemd를 제어하는 명령어
```shell
# 서비스 시작
systemctl start [서비스명].service

# 서비스 중지
systemctl stop [서비스명].service

# 서비스 재시작
systemctl restart [서비스명].service

# 서비스 설정 리로드 (서비스 중단 없이)
systemctl reload [서비스명].service

# 서비스 상태 확인
systemctl status [서비스명].service
```


## vs Cron

# systemd vs cron: 주요 차이점

| 기능 | systemd (timer) | cron |
|------|----------------|------|
| **기본 목적** | 시스템 및 서비스 관리 (타이머는 부가 기능) | 작업 스케줄링 전용 |
| **설정 위치** | `/etc/systemd/system/*.timer` 및 `*.service` | `/etc/crontab`, `/etc/cron.d/*`, 개별 사용자 crontab |
| **시간 표현** | 달력 시간(`OnCalendar=`) 또는 이벤트 기반(`OnBootSec=`) | 분-시-일-월-요일 형식 (예: `0 2 * * *`) |
| **로깅** | journald를 통한 통합 로깅 | syslog 또는 메일로 오류만 전송 |
| **의존성 관리** | 다른 서비스와의 의존성 설정 가능 | 의존성 관리 기능 없음 |
| **실패 처리** | 자동 재시도, 오류 처리 정책 설정 가능 | 실패 시 별도 처리 없음 |
| **누락된 작업** | `Persistent=true`로 시스템 중단 시 누락된 작업 처리 가능 | 시스템 중단 시 누락된 작업 복구 불가 |
| **정밀도** | 밀리초 단위 정밀도 | 1분 단위 정밀도 |
| **실행 환경** | 격리된 환경 설정 가능(cgroup, 네임스페이스 등) | 제한된 환경 설정 |
| **모니터링** | `systemctl list-timers`로 전체 상태 확인 | 직접적인 상태 확인 도구 제한적 |
| **보안** | 샌드박싱, 권한 제한 등 보안 기능 | 제한적인 보안 옵션 |
| **복잡성** | 상대적으로 복잡한 설정 | 간단한 구문과 설정 |
| **호환성** | 최신 Linux 배포판에서만 사용 가능 | 거의 모든 Unix/Linux 시스템에서 사용 가능 |
