# MetaData
- Batch 실행 및 관리를 위한 목적으로 여러 도메인 (Job, Step, JobParameters)의 정보들을 저장, 업데이트, 조회 기능을 지원하는 스키마 제공
- 과거, 현재의 실행에 대한 세세한 정보와 실행에 대한 성공, 실패 여부를 관리함으로서 배치 문제 발생 시 빠른 대처 가능
- DB와 연동할 경우, 필수적으로 MetaData 테이블이 생성되어야 한다.
    - /org/springframework/batch/core/schema-*sql 에 위치한다.
    - DB 유형별로 제공된다.
    - **자동 생성을 사용할 수는 있으나, 운영에서는 수동생성을 권장한다.**


## [1] BATCH_JOB_INSTANCE
- Job이 실행 될 때, JobInstance의 정보가 저장되며, job_name과 job_key를 unique 키로 하여, 하나의 데이터가 저장
- 동일한 job_name과 job_key로 중복 저장 될 수 없다.
  - job_key는 job_name과 jobParameter를 합쳐 해싱한 값이다.

## [2] BATCH_JOB_EXECUTION
- Job의 실행정보가 저장되며, job 생성, 시작, 종료시간, 실행상태, 메세지 등을 관리

## [3] BATCH_JOB_EXECUTION_PARAMS
- Job과 함께 실행되는 JobParameter 정보를 저장

## [4] BATCH_JOB_EXECUTION_CONTEXT
- Job의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화(Json) 하여 저장
- Step간 서로 공유 가능

## [5] BATCH_STEP_EXECUTION
- Step의 실행정보가 저장되며 생성, 시작, 종료시간, 실행상태, 메세지 등을 관리한다.

## [6] BATCH_STEP_EXECUTION_CONTEXT
- Step의 실행동안 여러가지 상태정보, 공유 데이터를 직렬화(Json) 하여 저장
- Step 별로 저장되며, Step간 서로 공유 할 수 없음