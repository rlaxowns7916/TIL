# SpringBatchTest
- https://docs.spring.io/spring-batch/reference/testing.html
- ```gradle
    testImplementation 'org.springframework.batch:spring-batch-test'
  ```
- SpringBatch의 Test에 필요한 기능 및 의존성을 제공한다.

## Test에 필요한 주요 Class들
- 자동으로 ApplicationContext에 Test를 위한 여러 Util Bean들을 등록해준다.
  - JobLauncherTestUtils
    - launchJob(), launchStep()과 같은 SpringBatch Test에 필요한 Util성 메소드를 제공한다.
  - JobRepositoryTestUtils
    - JobRepository를 사용해서 JobExecution 생성 및 삭제하는 Method를 지원한다.
  - StepScopeTestExecutionListener
    - @StepScope 컨텍스트를 생성해주며, 컨텍스트를 통해서 JobParameter 등을 DI 받을 수 있다.
  - JobScopeTestExecutionListener
    - @JobScope 컨텍스트를 생성해주며, 컨텍스트를 통해서 JobParameter 등을 DI 받을 수 있다.


## Test 기본 설정
```kotlin
@SpringBatchTest
@SpringBootTest(classes = [BatchTestConfig::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class SampleTest(
    private val jobLauncherTestUtils: JobLauncherTestUtils
) {

  @Test
  fun test(@Qualifier(value = "sampleJob") job: Job){
    //given
    jobLauncherTestUtils.job = job
    val jobParameters = JobParametersBuilder()
      .addString("name", "test")
      .toJobParameters()



    val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)
    val stepExecution = jobLauncherTestUtils.launchStep("step1").stepExecutions.associateBy { it.stepName }

    assertThat(stepExecution["step1"]).isNotNull())
    assertThat(stepExecution["step1"]!!.readCount).isEqualTo(1000)
    assertThat(stepExecution["step1"]!!.commitCount).isEqualTo(1000)

    assertThat(jobExecution.status).isEqualTo(BatchStatus.COMPLETED)
  }
}
```