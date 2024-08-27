# EventListener
- Batch 에서 Job, Step, Chunk 단계의 실행 전/후에 발생하는 이벤트를 받아 활용가능하게 하는 인터셉터
- 각 작업의 성공/실패 상관 없이 무조건 호출 

## 구현체 
1. Job
   - JobExecutionListener: Job 실행 전/후
2. Step
    - StepExecutionListener: Step 실행 전/후
    - ChunkListener: Chunk(Tasklet) 실행 전/후, 오류시점
    - ItemReadListener: ItemReader의 read 메서드 호출 전/후 (Item이 null 일 경우 호출 X)
    - ItemProcessListener: ItemProcessor의 process 메서드 호출 전/후 (Item이 null 일 경우 호출 X)
    - ItemWriteListener: ItemWriter의 write 메서드 호출 전/후 (Item이 null 일 경우 호출 X)
    - SkipListener: Read, Process, Write의 Skip 시점 (Skip된 Item 추적)
    - RetryListener: Retry 시작 / 종료 / 에러 시점

## 구현방법
### [1] Annotation
- Object 타입의 Listener로 등록하여 사용
```java
public class PerformanceAnalyzeJobExecutionListener {
    
    private val logger = LoggerFactory.getLogger(PerformanceAnalyzeJobExecutionListener.class);
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        logger.info("{} Started",jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        long duration = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        logger.info("{} Finished in {} ms",jobExecution.getJobInstance().getJobName(),duration);
    }
}
```

### [2] Interface
- Listener Interface를 구현하여 사용
```java
public class PerformanceAnalyzeJobExecutionListener implements JobExecutionListener {
    private val logger = LoggerFactory.getLogger(PerformanceAnalyzeJobExecutionListener.class);
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("{} Started",jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long duration = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        logger.info("{} Finished in {} ms",jobExecution.getJobInstance().getJobName(),duration);
    }
}
```