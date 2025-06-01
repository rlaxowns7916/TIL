# Lambda
- 일종의 함수이다.
  - 코드(함수)만 배포 → AWS가 자동으로 스케일링 및 관리
    - Memory 설정 가능 (128MB ~ 10GB)
    - RAM을 늘리면 CPU와 Network 성능도 비례해서 늘어난다.
  - 실행 시간이 지정되있다. (TimeOut은 최대 15분)
  - on-demand 로 실행된다. (필요할 때 실행)
    - API Gateway, S3, DynamoDB, Scheduled Event 등 다양한 이벤트를 트리거로 실행
  - 종속성은 배포 패키지를 통해 묶어서 업로드한다. (Jar or Container Image)
    - **배포 패키지는 압축 시 50MB, 압축 해제 시 250MB까지 가능하다.**
- Stateless 하다.
  - 각 함수는 격리된 환경에서 실행된다.
  - 이전 호출이나, 메모리 상태에 의존할 수 없다.
  - 상태를 유지해야 한다면, AmazonS3나 DynamoDB등을 활용해야 한다.
- AWS 모든 서비스들과 호환된다.
- 다양한 언어로 실행 가능하다. (**특정 언어에 따라 ColdStart가 발생할 수 있다.**)
  - Node.js
  - Python
  - Ruby
  - Java
  - ...
- Container Image도 지원한다.
  - Lambda Runtime API를 구현해야한다.
  - **하지만 Docker Image 실행에 있어서는 ECS나 Fargate가 더 선호된다.**
- 비용은 저렴한 편이다.
  - 비용은 실행횟수에 비례한다.
  - 1,000,000 Lambda 요청, 400,000GB Compute time 까지는 프리티어를 제공해준다.


## Serverless
- 물리, 가상 서버 관리 불필요
  - AWS Lambd가 최초였다.
  - DB, Storage 등 관리하지 않는 모든것을 의미한다.
    - AWS Lambda
    - Dynamo DB
    - S3
    - API Gateway
    - SNS & SQS
    - Cognito
    - ...

## Synchronous vs Asynchronous

### [1] Synchronous
- 실행하고 결과를 기다린다.
- Lambda 오류 발생 시, Client에 Handling 책임이 있다.
  - Retry (Exponential BackOff, Jitter, ...)
- ELB, API Gateway, CloudFront 등이 해당된다.

### [2] Asynchronous
- EventQueue에 Task가 저장되어 있고, 순차적으로 실행한다.
- 실패시, Retry를 시도한다.
  - 3 tries total
  - **함수를 멱등하게 구성해야한다.**
- DLQ를 구성 할 수 있다.
  - SNS, SQS
- S3, SNS, CloudWatch Events, EventBridge 등이 해당된다.