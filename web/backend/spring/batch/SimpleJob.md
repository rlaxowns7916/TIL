# SimpleJob
- 가장 기본적인 구현체
- SimpleJobBuilder에서 생성된다.
- 여러개의 Step으로만 구성 가능하며, 순차적으로 수행된다.
  - 순차 실행 도중, 중간 Step이 실패한다면 그 이후의 Step은 실행되지않는다.
- Step이 모두 완료되면 Job의 상태가 결정된다.
  - 모든 Step이 성공적으로 완료되었다면, Job은 성공적으로 완료된다.
  - 하나라도 실패했다면, Job은 실패한 것으로 간주된다.
- 내부적으로 steps라는 List를 가지고 있으며, 순차적으로 추가된다.
- start()와 next()로만 SimpleJob을 만들어낼 수 있다.

## 기본적인 구조
```kotlin
@Bean(JOB_NAME)
fun customJob(): Job{
    return JobBuilder(JOB_NAME,jobRepository)
        .start(Step) // Step
        .next(Step) // Step
        .incrementer(JobParametersIncrementer) // JobParameter 값을 자동적으로 증가시킴
        .preventRestart(true) //Job의 재시작 가능여부, 기본값은 true && false라면 실패를 했더라도 재시작이 불가능함 (JobRestartException 발생)
        .validator(JobParametersValidator) // Job을 실행하기 이전에, JobParameter를 검증하는 Validator
        .listener(JobExecutionListener) // Job LifeCycle의 특정 시점에, Callback을 제공받는 Listener 설정
        .build() // 생성
}
```

## Validator
- Job 실행에 필요한 JobParameters를 검증하는 용도이다.
  - 검증에 실패하면 Job이 실행되지 않는다.
- DefaultJobParametersValidator 구현체를 지원하며, 더 복잡한 제약 조건이 있다면 인터페이스를 직접 구현 할 수 있다.
  - Default의 경우 RequiredKeys(필수)와 OptionalKeys(선택)을 배열 형태로 지정 할 수 있다.
- Job이 수행되기 이전에 Validation 한다.

### 기본적인 구조
1. DefaultJobParametersValidator
```kotlin
@Bean(name = [SAMPLE_JOB])
    fun exampleJob(
        @Qualifier(value = SAMPLE_JOB_FIRST_STEP)
        firstStep: Step,
        @Qualifier(value = SAMPLE_JOB_SECOND_STEP)
        secondStep: Step
    ): Job {
        return JobBuilder(SAMPLE_JOB,jobRepository)
            .start(firstStep)
            .validator(DefaultJobParametersValidator(arrayOf("name"), arrayOf("age")))
            .build()
    }

//Caused by: org.springframework.batch.core.JobParametersInvalidException: The JobParameters do not contain required keys: [name]
```

2. 커스텀 구현
```kotlin
class CustomJobParameterValidator : JobParameterValidator{
    override fun validate(parameters: JobParameters?){
        if(parameters?.getString("name") == null){
            throw JobParametersInvalidationException("name not found")
        }
      // ...
    }
}
```

## Incrementer
- JobParameter에서 필요한 값을 증가시켜, 다음에 사용될 JobParameters 오브젝트를 리턴한다.
- **기존의 JobParameter 변경 없이, Job을 여러번 시작하고싶을 때 사용한다**
- RunIdIncrementer 구현체를 지원한다.
- JobParametersIncrementer를 구현하여, Custom하게 생성도 가능하다.

### Incrementer 호출 시점
```java
/**
 *  Incrementer의 Parameter 개입은 JobInstance 존재 여부 확인 이후에 동작한다.
 *  만약 처음부터 Incrementer를 사용하지 않았고, 필수파라미터를 사용한 JobInstnace가 MetaData에 이미 존재한다면,
 *  영원히 실패한다. (이전의 Instance를 그대로 사용할 것이기 떄문)
 * 
 *  처음부터 Incrementer를 사용하거나, 필수파라미터로 이루어진 JobInsttance MetaData삭제가 필요하다.
 */

// JobLauncherApplicationRunner (SpringBoot 기본 Runner) 발췌
private JobParameters getNextJobParameters(Job job, JobParameters jobParameters) {
        if (this.jobRepository != null && this.jobRepository.isJobInstanceExists(job.getName(), jobParameters)) {
            return this.getNextJobParametersForExisting(job, jobParameters);
        } else if (job.getJobParametersIncrementer() == null) {
            return jobParameters;
        } else {
            JobParameters nextParameters = (new JobParametersBuilder(jobParameters, this.jobExplorer)).getNextJobParameters(job).toJobParameters();
            return this.merge(nextParameters, jobParameters);
        }
    }
```

### [1] RunIdIncrementer
```java
public JobParameters getNext(@Nullable JobParameters parameters) {
        JobParameters params = parameters == null ? new JobParameters() : parameters;
        JobParameter<?> runIdParameter = (JobParameter)params.getParameters().get(this.key);
        long id = 1L;
        if (runIdParameter != null) {
            try {
                id = Long.parseLong(runIdParameter.getValue().toString()) + 1L;
            } catch (NumberFormatException var7) {
                throw new IllegalArgumentException("Invalid value for parameter " + this.key, var7);
            }
        }

        return (new JobParametersBuilder(params)).addLong(this.key, id).toJobParameters();
    }
```

### [2] CustomIncrementer