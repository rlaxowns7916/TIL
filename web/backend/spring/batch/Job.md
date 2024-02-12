# Job
- Batch 계층 최상위에 있는 개념으로, 하나의 작업 그자체를 의미함
- JobConfiguration을 통해 생성되는 객체의 단위이며, Batch작업을 어떻게 구성하고 실행할 것인지 명시해놓은 객체
- Job을 구성하기 위한 최상위 구현체이며, SpringBatch에서 기본 구현체를 제공한다.
- 여러 Step을 포함하고있으며, 최소 한개 이상의 Step을 가지고 있어야한다.
- 명세서 혹은 설계도 라고 볼 수 있다.

## 구현체
```java
    /**
      * JobBuilder 내부 코드
      */
    public SimpleJobBuilder start(Step step) {
        return (new SimpleJobBuilder(this)).start(step);
    }

    public JobFlowBuilder start(Flow flow) {
        return (new FlowJobBuilder(this)).start(flow);
    }
```
### [1] SimpleJob
- 순차적으로 Step을 실행시킬 수 있는 Job
- 모든 Job에서 유용하게 사용할 수 있는 표준 기능을 가지고 있음
- 내부적으로 Step들을 List로 관리한다.
- JobBuilder에서 start(step)메소드 호출 시, SimpleJobBuilder가 내부적으로 생서된다.
- SimpleJobBuilder 인자 이후에, FlowJob연산 (on, ...)을 사용하면 자동으로 FlowJob으로 변경된다.

### [2] FlowJob
- 특정한 조건의 흐름에 따라 Step을 구성하고 실행시킨다.
  - 조건부 실행
  - 분기
  - 반복
- Flow객체를 실행시켜서 작업을 수행한다.
  - Decider라는 객체를 통해서, Step실행 후의 상태를 평가하고 다음에 어떤 Step이나 Flow를 수행할지 판단한다.
- JobBuilder에서 flow(step)메소드 호출 시, FlowJobBuilder가 내부적으로 생서된다.


## 실행흐름
1. JobLauncher가 JobParameter를 포함하여 Job을 실행
2. Job이 포함된 Step들을 execute
3. Step이 자신의 Tasklet을 실행


---

## JobInstance
- Job이 실행되는 시점의 객체
  - 일 배치라면, 매일 새로운 JobInstance가 생성된다.
- 고유하게 식별가능한 작업실행을 나타낸다.
  - 일 배치라면, Job의 설정은 같을테지만 실행되는 처리하는 시점의 내용은 다를 것이다.
  - 이전과 동일한 jobName + jobKey(JobParameter의 Hash) 라면 이미 존재하는 JobIntance를 리턴한다.
    - JOB_NAME(Job)과 JOB_KEY(JobParameter의 Hash값)가 동일한 데이터는 중복해서 저장 할 수 없다.

### 생성과정
1. JobLauncher가 Job을 실행 // run(job, jobParameters);
2. JobRepository에서 Job과 JobParameters를 조회
3. 이미 존재한다면 기존 JobInstance를 반환해고 JobInstanceAlreadyCompleteException 발생 (Job을 실행하지 않음)
4. 존재하지 않았다면 새로운 JobInstnace를 생성


---

## JobLauncher
- job을 실행시킨다.
- Job과, JobParameters를 인자로 받는다.
- SpringBoot에서는 자동 실행이 옵션으로 되어있다.
  - JobLauncherApplictionRunner가 자동으로 실행시킨다. 
  - 아래의 설정을 통해 자동실행을 막을 수 있다.
  - ```yaml
      spring:
          batch:
            job:
              enabled: false
    ```
- 배치 작업 수행 후, Client에게 JobExecution을 반환한다.


### 동기 / 비동기 실행
1. 동기적 실행
  - TaskExecutor를 SyncTaskExecutor로 사용할 경우 (기본 값)
  - Batch처리가 길어도 상관없는 경우에 주로 사용한다.
  - JobExecution을 획득 한 후, Batch처리를 최종 완료 한 후 Client에게 반환한다.
    - EXIT_STATUS는 FINISHED || FAILED이다.
2. 비동기적 실행
   - TaskExecutor를 SimpleAsyncTaskExecutor로 사용할 경우
   - JobExecution을 획득 한 후, 바로 Client에게 반환하며, 그 이후 Batch작업을 수행한다.
     - EXIT_STATUS는 UNKNOWN이다.
   - HTTP 요청에 의한 Batch 처리에 적합하다.

---
## JobParameters
- Job을 실행할 때, 함께 포함되어 사용되는 Parameter를 가진 도메인 객체
- **BATCH_JOB_EXECUTION_PARAMS 테이블에 매핑된다.**
  - **JOB_EXECUTION과 1:N관계이다. (parameters가 N)**
- JobInstance들을 구분하는 것이 주요 목적이다.
- 모든 타입을 지원  (5.0이전버전은 STRING, DATE, LONG, DOBULE만 지원)
- 내부적으로 LinkedHashMap으로 구현되어있다.

### 생성법
1. Applicaiton 실행 시 주입
   - ```curl
        # 5.0 <= : parameterName=parameterValue,parameterType,identificationFlag
            - age=28,java.lang.Long,Y name=kimatejun
     
     
        # 5.0 >  : parameterName(parameterType)=parameterValue
            - age(long)=28 name=kimtaejun
        java -jar batch-applicaiton.jar targetDate=20240211
     ```
2. 코드로 생성
   - JobParameterBuilder
   - DefaultJobParametersConverter
3. SPEL 이용
   - @Value("#{jobParameter[requestDate]}")
   - @JobScope
   - @StepScope


## JobExecution
- JobInstance에 대한 한번의 시도를 의미한다.
  - Job실행 중에 발생한 정보들을 저장하고 있다.
- JobInstance의 상태에 따라서 재실행 여부가 결정된다.
  - FAILED이면 재실행이 가능하다.
  - COMPLETED이면 완료된 것으로 간주되어 재실행이 불가능하다.
  - 새롭게 JobInstnace가 생성되거나, 이미생성된 JobInstance가 FAIL상태라면 새롭게 JobExecution 객체를 생성할 수 있다.
- JobInstance에 대한 성공 / 실패 내역을 가지고 있다.
  - 1:N관계이다.
- 크게 두가지의 상태를 갖는다.
  - STATUS: 실행상태
    - COMPLETED,
    - STARTING,
    - STARTED,
    - STOPPING,
    - STOPPED,
    - FAILED,
    - ABANDONED,
    - UNKNOWN;
  - EXIT_CODE: 실행결과
    - UNKNOWN
    - EXECUTING
    - COMPLETED
    - NOOP 
    - FAILED 
    - STOPPED
---

## JobExecutionContext
- Key/Value로 관리된 Collection이다. (ConcurrentHashMap)
- DB에 직렬화 된 값으로 저장한다.
- 각 Job의 JobExecutionContext에 저장된다.
- Job내의 Step들 끼리 서로 공유가 가능하다.
- 너무많이 저장하면 성능상의 문제가 일어날 수 있다. (직렬화 / 역직렬화 과정)


## JobRepository
- 배치 작업 중, 정보를 저장하는 저장소 역할
- Job이 언제 수행되었고, 언제 끝났고, 몇번 실행되었는지 등의 MetaDat를 저장한다.
- JobLauncher, Job, Step 구현체 내부에서 CRUD를 처리한다.
- MetaData 연동에필요한 여러가지 Dao들을 모두 가지고있다.

## JobRepository 설정
- BatchConfigurer나, BasicBatchConfigurer를 상속해서 JobRepository 설정을 커스터 마이징 가능하다.
- JDBC 방식 (JobRepositoryFactoryBean)
  - 내부적으로 AOP를 통한 트랜잭션 수행
  - Transaction 기본 값은 SERIALIZABLE (다른 레벨로 변경가능)
  - MetaTable의 PREFIX 변경 가능 (default: BATCH_)
- InMemory 방식 (MapJobRepositoryFactoryBean)
  - 성능 등의 이유로 Database에 저장하기 싫은 경우 사용
  - 보통 Test나, Prototype 개발이 필요할 때 사용
  - ResourcelessTransactionManger를 사용하면 된다. (DataSource를 비워주면 자동으로 설정된다.)
- Custom
  - BasicBatchConfigurer 상속
  - createJobRepository 오버라이드

## MetaData 사용하지 않기
```java
/**
 * DefaultBatchConfigurer 발췌
 * ResourcelessTransactionManager를 사용하기 때문에, MetatData Table을 사용하지 않게된다.
 */
@PostConstruct
    public void initialize() {
        try {
            if (this.dataSource == null) {
                logger.warn("No datasource was provided...using a Map based JobRepository");
                if (this.getTransactionManager() == null) {
                    logger.warn("No transaction manager was provided, using a ResourcelessTransactionManager");
                    this.transactionManager = new ResourcelessTransactionManager();
                }

                MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean(this.getTransactionManager());
                jobRepositoryFactory.afterPropertiesSet();
                this.jobRepository = jobRepositoryFactory.getObject();
                MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
                jobExplorerFactory.afterPropertiesSet();
                this.jobExplorer = jobExplorerFactory.getObject();
            } else {
                this.jobRepository = this.createJobRepository();
                this.jobExplorer = this.createJobExplorer();
            }

            this.jobLauncher = this.createJobLauncher();
        } catch (Exception var3) {
            throw new BatchConfigurationException(var3);
        }
    }
```

---
