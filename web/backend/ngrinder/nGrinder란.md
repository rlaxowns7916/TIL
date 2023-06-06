# nGrinder란
- Naver에서 만든 오픈소스
  - GUI를 통해서 테스트 가능 
  - **Groovy스크립트를 통해서 테스트 가능
- JUnit, Groovy 기반으로 테스트를 실행한다.
- home > .ngrinder에 저장된다.


## Controller
- 성능/부하 테스트 진행을 위한 웹 인터페이스 제공
- 테스트 통계를 수집하거 표시
- 테스트 프로세스/스크립트 수정

### 설치
- https://github.com/naver/ngrinder/releases
  - 위 경로에서 war파일을 다운 받을 수 있다.

### 실행
- ```shell
  $ java -Djava.io.tmpdir=/${실행시 필요한 임시파일을 저장할 경로} -jar ${nGrinder war파일} --port ${실행 Port}  
  ```
- 실행 기본 id / pw = admin / admin


## Agent
- 대상 시스템에 부하를 주는 프로세스 및 스레드 실행
- 모니터 모드에서 실행 시, 해당 시스템의 (CPU/메모리) 모니터링

### 설치
- Admin Web > Download Agent

### 실행
1. 다운로드 후 압축 풀기
2. agent.conf에서 Host나 Port 변경이 가능하다.
3. 실행 명령어를 수행한다.
  - ```shell
    $ ./run_agent.sh
    ```
4. Admin Web > AgentManagement > Agent Approved


## 실행 시 오류 

### [1] Controller - Local IP Address 확인 오류
```text
org.springframework.beans.factory.UnsatisfiedDependencyException:    
Error creating bean with name 'org.ngrinder.perftest.service.PerfTestService': Unsatisfied dependency expressed through constructor parameter 1; 
nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'consoleManager': Invocation of init method failed; 
nested exception is org.ngrinder.common.exception.NGrinderRuntimeException: Can not check available ports because given local IP address '218.38.137.27' is unreachable.
Please check the `/etc/hosts` file or manually specify the local IP address in `${NGRINDER_HOME}/system.conf
```
- **NGRDINER_HOME (/Users/.ngrinder)의 system.conf에서 ngrdiner.contgroller.ip 설정**


### [2] Agent - Java 버전 문제
```text
net.grinder.engine.common.EngineException: Error while initialize test runner
	at net.grinder.scriptengine.groovy.GroovyScriptEngine.<init>(GroovyScriptEngine.java:71)
	at net.grinder.scriptengine.groovy.GroovyScriptEngineService.createScriptEngine(GroovyScriptEngineService.java:87)
	at net.grinder.engine.process.ScriptEngineContainer.getScriptEngine(ScriptEngineContainer.java:105)
	at net.grinder.engine.process.GrinderProcess.run(GrinderProcess.java:345)
	at net.grinder.engine.process.WorkerProcessEntryPoint.run(WorkerProcessEntryPoint.java:87)
	at net.grinder.engine.process.WorkerProcessEntryPoint.main(WorkerProcessEntryPoint.java:60)
Caused by: org.codehaus.groovy.GroovyBugError: BUG! exception in phase 'semantic analysis' in source unit '/Users/taejukim/.ngrinder_agent/file-store/admin/current/Sample-Script.groovy' Unsupported class file major version 61
```
- Java17을 아직 지원하지 않아서 발생하는 문제
- JAVA_HOME에서 8 혹은 11로 변경하면 된다.