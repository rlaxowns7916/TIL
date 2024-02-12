# Batch 실행 설정

## BatchProperties
- SpringBatch의 환경 설정 Class
- Job이름, 스키마 초기화 설정, 테이블 Prefix등을 정의 할 수 있다.
- application.yml에 정의 가능하다.
  - ```yaml
    spring:
      batch:
        job:
          names: sampleJob # 여기 있는 것들이 실행된다.
          enabled: false # 실행 여부 (default: true // 정의된 모든 Job을 실행한다) 
        schema:
          initialize: NEVER
        table-prefix: BATCH_
    ```
    
### spring.batch.job.names 필터링 코드
- 여러개 실행을 원할 경우 ,를 구분자로 job의 이름을 정의한다.
- 정의되어있는 Job들을 루프돌면서, 일치하는 JobName만 실행한다.
```java
private void executeLocalJobs(JobParameters jobParameters) throws JobExecutionException {
        Iterator var2 = this.jobs.iterator();

        while(true) {
            while(var2.hasNext()) {
                Job job = (Job)var2.next();
                if (StringUtils.hasText(this.jobName) && !this.jobName.equals(job.getName())) {
                    logger.debug(LogMessage.format("Skipped job: %s", job.getName()));
                } else {
                    this.execute(job, jobParameters);
                }
            }

            return;
        }
    }
```
