# Spring Batch

## 핵심 패턴
- Read: DB, File, Queue에서 다량의 데이터 조회
- Process: 데이터를 가공
- Write: 가공한 데이터를 다시 저장한다.   
**ETL(Extract - Transform - Load)와 동일하다.**

## 배치 시나리오
- Batch Process를 주기적으로 Commit
- 동시 다발적인 Job Batch처리, 대용량 병렬 처리
- 실패 후, 수동 혹은 스케줄링에 의한 재시작
- Step을 의존관계에 따라 순차처리
- 조건적 Flow를 통한 유연한 Batch모델 구성
- 반복, 재시도, Skip 처리

## 아키텍처
- 실제 의존성으로 존재한다.
### [1] Application
- SpringBatch를 통해서 만든 BatchJob과 CustomCode가 포함
- 비즈니스 로직만을 구현

### [2] Batch Core
- Job을 실행, 모니터링 관리하는 API로 구성
- JobLauncher, Job, Step, Flow등이 속한다.

### [3] Batch Infrastructure
- Application, Core를 빌드하는 틀이 된다.
- Job실행의 흐름과 처리를 위한 틀을 제공한다.
- Reader, Processor, Writer, Skip, Retry등이 속한다.