# Jenkins란
- CI/CD 구성에 있어 Workflow를 제어한다.
- 다양한 plugin과 연동 된다.
- JRE에서 동작한다.
- 오픈소스

## Jenkins 수행 기능
- 지속적인 통합 (CI)
- Jenkins 파이프라인을 통한 배포 전달 (CD)
- 플러그인을 통한 정적분석 (SonarQube ...)
- 배치 프로세스 관리
- 인프라 요소 배포 및 관리

## 설치
```shell
$ docker pull jenkins/jenkins:jdk11
$ docker run -d --name jenkins -p 8080:8080 -p 50000:50000 --restart=on-failure
```

### JDK 경로지정
- 기본적으로 JRE위에서 돌기 떄문에, JDK설정을 해주어야한다.
- Manage Jenkins > Global Tool Configuration > JDK
  - 다운 받을 수도 있지만, 9버전이 최신 & Oracle 계정 필요

## 최소 하드웨어 요구사항
- 256MB RAM
- 10GB 드라이브 공간 (Docker로 실행시)