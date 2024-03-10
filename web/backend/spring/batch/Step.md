# Step
- BatchJob을 구성하는 독립적인 단계
  - 실제 Batch처리를 정의하고 컨트롤하는데 필요한 정보를 가지고 있다.
  - 단순 Task뿐만 아니라, Input, Processing, Output과 관련된 비즈니스 로직을 담고 있다.
- 모든 Job은 하나이상의 Step으로 구성된다.
- Batch의 작업순서를 정의한 객체

## 기본 구현체
1. TaskletStep
   - 가장 기본
   - Tasklet과 Chunk는 동시에 설정 할 수 없다.
   - Tasklet방식 (단일), Chunk방식(일정량을 나누어 처리) 2가지 모두 TaskletStep을 통해서 구현 가능하다.
   - ```kotlin
        @Bean
        fun taskletStep(): Step{ 
            return StepBuilder(SAMPLE_JOB,jobRepository)
                .tasklet(tasklet(),transactionManger)
                .build()
        }
        
        @Bean
        fun chunkStep(): Step{
            return StepBuilder(SAMPLE_JOB,jobRepository)
                .chunk(chunkSize,transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build()
        }
     ```
2. PartitionStep
   - 멀티스레드 방식으로 Step을 실행
3. JobStep
   - Step내에서 Job을 실행
   - ```kotlin
      fun jobStep() :Step{
        return StepBuilder(SAMPLE_JOB,jobRepository)
                .job(job())
                .launcher(jobLauncher)
                .parametersExtractor(jobParameterExtractor())
                .build()   
     }
     ```
4. FlowStep
   - Step내에서 Flow를 실행
   - ```kotlin
      fun flowStep(): Step{
        return StepBuilder(SAMPLE_JOB,jobRepository)
                .flow(flow())
                .build()
      }
     ```
 
## StepBuilder
1. TaskletStepBuilder
   - tasklet 생성
2. SimpleStepBuilder
   - chunk-oriented-tasklet 생성
3. PartitionStepBuilder
   - multi-thread 방식
4. JobStepBuilder
   - step안에 job을 넣는 방식
5. FlowStepBuilder
   - step안에 flow를 넣는 방식

## StepExecution
- Step에 대한 한번의 실행을 의미하는 객체
  - 시작시간, 종료시간, 상태(시작,완료,실패) 등의 정보를 저장하고 있다.
- Step이 매번 실행될 때마다 생생되며, 각 Step별로 생성된다.
- **Job이 재시작 될 때, 이미 성공적으로 완료된 Step은 재실행되지 않고, 실패한 Step만 재실행된다.**
  - Step 재시작 옵션을 준다면, 성공했어도 재시작 할 수 있다.
- 이전 Step이 실패했을 경우, 그다음 Step은 실행되지 않을 것이고 STEP_EXECUTION또한 생성되지 않는다.
- **Job에 소속된 STEP_EXECUTION 중 하나라도 실패 시, JOB_EXECUTION 또한 실패한다.**
- JobExecution과 1:N 관계를 갖는다. (각 StepExecution은 JobExecution을 부모로 갖는다.)

---

## StepContribution
- Step의 변경사항을 버퍼링 한 후, StepExecution 상태를 업데이트하는 객체이다.

### StepContribution이 관리하는 정보
- readCount: 성공적으로 Read한 Item의 수 (ItemReader)
- writeCount: 성공적으로 Write한 Item의 수 (ItermWriter)
- filterCount: 성공적으로 Process한 Item의 수 (ItermProcessor)
- parentSkipCount: 부모 클래스인 StepExcution의 총 Skip 횟수
- readSkipCount: Read에 실패해서 Skip된 횟수
- writeSkipCount: Write에 실패해서 Skip된 횟수
- processSkipCount: Process에 실패해서 Skip된 횟수
- ExitStatus: 실행결과를 나타내는 상태 (사용자 정의 종료코드도 사용가능)

### 생성과정
1. Step이 StepExecution을 생성
2. StepExecution이 StepContribution을 생성
3. Step이 결과를 StepContribution에 업데이트
4. StepContribution이 StepExecution을 업데이트 (apply)

---

## StepExecutionContext
- Key/Value로 관리된 Collection이다. (ConcurrentHashMap)
- DB에 직렬화 된 값으로 저장한다.
- 각 Step의 StepExecutionContext에 저장된다.
- Step끼리 공유는 불가능하다.

## 구현체

### [1] TaskletStep
- Step의 구현체, Tasklet을 실행시키는 도메인 객체 
  - **Step에는 오직 하나의 Tasklet만 설정 가능하며, 가장 마지막으로 등록된 Tasklet이 실행된다.**
- RepeatTemplate을 사용한다.
  - Tasklet의 구문을, Transaction 범위내에서 반복해서 실행한다.
    - Transaction 내부에 있기 때문에, Commit, Rollback에서 자유롭다 (별도의 Transaction 처리를 할 필요는 없다)
    - ex) chunk 1,2,3,4,5가 있을 떄 chunk3이 실패했다면 1과2는 정상적으로 Commit, 3은 반영(X) 그뒤의 것들은 실행(일반적으로 X)
  - RepeatStatus를 통해서 Tasklet의 종료 및 반복을 설정 할 수 있다. 
    - RepeatStatus.CONTINUABLE
    - RepeatStatus.FINISHED (null도 Finished로 인식된다.)
    - Task기반과 Chunk기반으로 분리된다.
      - Task기반
        - 단일 작업으로 처리하는 것이 유리할 때 사용한다.
        - 주로 Tasklet을 구현하여 사용하며, 대용량 처리를 할 때는 Chunk기반보다 구현이 복잡해진다.
        - ```kotlin
              fun step(): Step{
                  return StepBuilder(SAMPLE_JOB_FIRST_STEP,jobRepository)
                      .tasklet(tasklet,transactionManager)
                      // Step의 실행횟수를 설정 StepExeuction에 쌓이는 Row의 갯수 (default: Integer.MAX_VALUE), 초과시 오류 발생
                      .startLimit() 
                      //Step의 성공, 실패에 상관없이 재시작 가능여부
                      .allowStartIfComplete()
                      // Step의 LifeCycle에서 특정시점에 Callback을 받기위한 Listener
                      .listener() 
                      .build()
              }
          ```
      - Chunk기반
        - ItemReader, ItemProcessor, ItemWriter를 사용할 수 있다.
        - Chunk기반은 Reader, Processor, Writer의 단위가 Transaction으로 묶인다.
        - 대량 처리를 수행할 때 효과적이다.
        - ```kotlin
               fun chunkStep(): Step {
                    return StepBuilder(SAMPLE_JOB_FIRST_STEP + "chunked", jobRepository)
                      .chunk<String,String>(100)
                      .transactionManager(transactionManager)
                      .reader()
                      .processor()
                      .writer()
                      .build()
               }
          ```
### [2] JobStep
- Step안에서 실행되는 Job이다.
  - MetaData는 기본 Job과 외부 Job으로 구분된다. (JobInstance, JobExecution ...) 
  - 해당 Job이 실패하면 Step이 실패하는 것과 마찬가지이기 떄문에 최종 Job도 실패하게된다.
- 작은 모듈로 쪼개고, Job의 흐름을 관리하고자 할 때 사용할 수 있다.
- 부모 Job의 Parameters를 그대로 사용 가능 (필요한 Parameter는 부모쪽에 다 넘기자)
- ```text
     ParentJob
        JobStep (Step1)
            ChildJob1
              ChildJob's Step
        Step2 
  ```
- ```kotlin
    fun jobStep(): Step{
        return StepBuilder("jobStep",jobRepository)
            .job() 
            .launcher()
            .parametersExtractor() 
            .build() 
    }
    ```