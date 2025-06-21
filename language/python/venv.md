# venv
- Python이 실행될 수 있는 가벼운 가상환경
- 명시적으로 선언된 패키지를 사용할 수 있다.
- 생성될 때의 Python 버전으로 고정된다.
  - 버전을 변경하려면 재생성 해야한다. 
  - ```shell
        # Python 3.9로 venv 생성
        python3.9 -m venv myenv
        
        # Python 3.11로 별도 venv 생성
        python3.11 -m venv myenv311
    ```
- 대안 도구들도 존재한다.
  - virtualenv, pipenv, poetry 등

## 특성

### [1] 의존성 충돌 방지 (Dependency Isolation)
- venv는 각 프로젝트마다 독립된 Python 인터프리터와 site-packages 디렉토리를 제공하여 이러한 충돌을 방지
- 전역 패키지와, 특정 프로젝트가 공통으로 사용하는 패키지의 버전 상이한 경우에도 정상적으로 작동하게한다.
    - A 프로젝트는 Django==2.2가 필요하고
    - B 프로젝트는 Django==4.0이 필요한 상황


### [2] 프로젝트별 패키지 관리
- requirements.txt 파일을 통해 각 프로젝트에 필요한 패키지를 명시적으로 관리
- 각 프로젝트에 필요한 라이브러리만 설치하여 경량화할 수 있으며, 불필요한 의존성을 줄일 수 있다. 
  - 디스크 사용량 최소화 
  - Docker 이미지 빌드시 용량 감소 
  - 배포 시 필요한 최소 패키지만 포함 가능


## [3] 재현 가능한 환경 구성
-  로컬, CI/CD 환경에서 pip install -r requirements.txt로 정확히 동일한 환경을 복원 가능
- 테스트 시 환경 차이로 인한 flaky 테스트 방지
- 배포 서버와 개발 서버 간 환경 차이 제거

## [4] 보안 및 시스템 보호
- 전역환경과, 프로젝트 환경을 분리하여 의존성을 끊어내 시스템 보호가 가능하다.

## vs JVM Build (Maven, Gradle 등)
- JVM의 경우 Build 시점에 의존성을 관리하기 때문에 Runtime 시점에는 이미 의존성이 모두 격리된 상태
- Python의 경우 Runtime에 패키지를 직접 로드
  - 패키지들이 site-packages 디렉토리에 설치되어 있기 때문에, venv를 통해서만 의존성을 격리할 수 있다.

| 구분 | JVM (Maven/Gradle) | Python (pip + venv) |
|---|---|---|
| **의존성 해결 시점** | 컴파일 타임 | 런타임 |
| **의존성 저장 위치** | JAR 내부 포함 | 파일 시스템 디렉토리 |
| **격리 메커니즘** | 클래스패스 자동 격리 | venv로 수동 격리 |
| **배포 산출물** | Fat JAR (의존성 포함) | 소스코드 + requirements.txt |
| **의존성 충돌 해결** | 빌드 도구가 자동 해결 | venv로 환경 분리 |
| **버전 관리** | pom.xml/build.gradle | requirements.txt |
| **실행 환경** | JVM (격리된 프로세스) | Python 인터프리터 (전역 공유) |
| **패키지 검색** | 클래스패스 순서 | sys.path 순서 |
| **다중 버전 지원** | 클래스로더로 자동 분리 | venv로 수동 분리 |
| **설치 명령** | `mvn install` / `gradle build` | `pip install` |
| **의존성 트리** | 빌드 도구가 관리 | pip이 단순 설치만 담당 |
| **네이티브 라이브러리** | JNI로 제한적 사용 | C 확장 모듈 직접 사용 |
| **캐시 위치** | `~/.m2` / `~/.gradle` | `~/.cache/pip` |
| **Lock 파일** | 자동 생성 (Maven: 없음, Gradle: gradle.lockfile) | 수동 생성 (pip freeze) |