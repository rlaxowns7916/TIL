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