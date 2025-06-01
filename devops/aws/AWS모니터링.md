# AWS 모니터링

## CloudWatch
- Metic, Log를 수집하고 분석 할 수 있다.
- 특정 Event가 발생했을 때, 알람을 설정할 수 있다.
  - Ec2 AutoScaling, AWS SNS등 다양한 자동화 설정이 가능해진다.

### 주요 용어
| 용어         | 설명                                                         |
|--------------|--------------------------------------------------------------|
| Namespace    | 지표를 논리적으로 구분하는 단위 (예: EC2, RDS)              |
| Dimension    | 지표를 세부적으로 분류하는 속성 (예: 인스턴스 ID)          |
| Metric       | 수집되는 개별 데이터 포인트 (예: CPU 사용률)               |
| Statistic    | 지표의 평균, 최대, 최소 등 통계값                           |
| Log Group    | 관련 로그 스트림을 묶는 컨테이너                            |
| Log Stream   | 개별 로그 이벤트 시퀀스                                     |

### Log
- LogGroup을 지정해야한다.
  - 내부에 여러개의 LogStream을 갖는다.
  - LogStream: application/ log-files/ containers/ ...
- Log의 Retention을 지정할 수 있다.
    - 만료없음, 1~10년, ...
    - Retention 설정이 변경되면, 그 이외의 것들은 즉시 삭제된다.
- 여러개의 Source가 존재한다.
  - SDK, CloudWatch LogAgent, CloudWatch Unified Agent(Deprecated)
  - Elastic Beanstalk
  - ECS
  - AWS Lambda
  - VPC Flow Logs
  - Route53 DNS Query
- CloudWatch Logs Insights를 통해서 Log를 검색 할 수 있다.
  - QueryEngine이다. (실시간 Engine이 아님)
- S3 Export
  - CreateExportTask API를 사용하여, S3에 Export할 수 있다.
  - 배치 방식이며 최대 12시간이 걸릴 수 있다.
    - **실시간은 CloudWatchLogs Subscrition을 사용해야한다..**
- CloudWatchLogs Subscription
  - Log 이벤트를 실시간으로 스트리밍 받는다.
    - 필터링이 가능하다.
  - 다양한 곳으로 Log를 전송이 가능하다.
    - OpenSearch
    - S3
    - Lambda
    - Kinesis Data Stream
    - Kinesis Data Firehose
  - 여러 Region에서 발생한 Log를 수집가능하게 한다.
### Metric
- AWS에서 제공하는 성능 지표 (모든 서비스에 대해 제공)
  - Metric은 Namespace에 속한다.
  - Metric은 Dimension으로 구분된다. 
    - Dimension은 Key-Value 쌍으로 이루어져 있다. (instance-id, environment ...)
    - 최대 10개까지 설정 가능하다.

### Custom Metrics
- 사용자가 직접 정의한 Metric (ex. MemoryUsage)
- CloudWatch Agent를 설치하여, PutMetricData API를 사용하여 Push한다.
  - 1분 (default), 
  - High-Resolution(1, 5, 10, 30) 단위로 Push 가능하다.(비용)
- **Push 시 과거, 미래를 상관하지않는다. (오류가 발생하지 않음)**

### EC2 Monitoring
- 기본적으로 5분마다 Metric을 모은다. (비용이 드는 옵션으로 1분마다도 Enable 가능하다.)
- AWS Free Tier에서 제공하는 CloudWatch는 10개까지의 Custom Metric을 무료로 제공한다.
  - MemoryUsage는 Custom Metrics 이며, 기본적으로 제공되는 Metric이 아니다. (Push 설정 필요)

### Alarms
- CloudWatch에서 제공하는 알람 기능
- 특정 Metric이 설정한 Threshold를 초과하면 알람을 발생시킨다.
- AugoScaling을 한다던가, SNS와 연결하여 알림을 받거나, Lambda를 호출하는 등의 작업을 수행할 수 있다.
- Composite Alarm을 사용하여, 여러개의 Alarm을 조합하여 알람을 발생시킬 수 있다.

### Synthetic Canary
- CloudWatch Synthetics를 사용하여, 웹사이트나 API의 가용성을 모니터링 할 수 있다.
- Node/Python 스크립트로 실행이 가능하다.
- Canary는 주기적으로 웹사이트나 API를 호출하여 응답을 확인한다.
  - HTTP 상태코드
  - 응답시간
  - 단계별 오류
  - 스크린샷 비교
  - ...
- CloudWatch와 통합된다.
- 사용사례
  - API 헬스 체크: 내부·외부 API의 정상 작동 여부 상시 검증
  - 웹 사이트 모니터링: 로그인, 검색, 장바구니 등 사용자 흐름 점검
  - CI/CD 파이프라인: 배포 후 자동 Smoke Test용 Canary 실행
  - 비주얼 회귀 테스트: 웹페이지 UI 변경 시 비주얼 리그레션 감지

## Event Bridge
- AWS 서비스와 SaaS 애플리케이션 간의 이벤트를 연결하는 서버리스 이벤트 버스
  - Saas로부터 이벤트를 받을 수 있는 Partner EventBus도 존재한다. (ex. Datadog, Zendesk, ...)
  - Custom Appplication으로 부터 이벤트를 받을 수 있는 Custom EventBus도 존재한다.
- 이벤트를 수집하고, 필터링하여, 다른 AWS 서비스로 전달한다. (필터링도 가능)
  - 다양한 Source를 가진다.
     - EC2
     - CodeBuild
     - S3
     - cron (scheduled)
     - ...
  - 다양한 Destination을 가진다.
     - Lambda
     - SNS
     - SQS
     - Kinesis Data Firehose
     - API Gateway
     - EventBridge Archive
     - ...
- 여러계정의 Event를 하나의 계정의 EventBridge로 수집도 가능하다.
- Schema Registry
  - EventBridge에서 발생하는 이벤트의 스키마를 정의하고 관리하는 기능
  - JSON Schema 형식으로 정의된다.
  - AWS CLI, SDK, 콘솔을 통해서 스키마를 생성하고 관리할 수 있다.
  - EventBridge에서 발생하는 이벤트를 자동으로 감지하여 스키마를 생성할 수 있다.
  - AWS Glue와 통합되어 데이터 카탈로그에 스키마를 저장할 수 있다.
## X-Ray
- AWS에서 제공하는 분산 추적 서비스
- 어플리케이션의 성능을 분석하고, 문제를 진단하는 데 도움을 준다. (시각적 분석 제공)
  - bottleneck
  - latency
  - error/exception
  - SLA 충족 여부
  - 영향받은 User
- 다양한 AWS 서비스와 호환된다.
  - AWS Lambda
  - Amazon EC2
  - Amazon ECS
  - Amazon ELB
  - Api Gateway다

### Enable하는 방법
1. SDK
   - Application(Java, Node.js, Python, Go, .NET)에서 SDK를 사용하여 X-Ray에 데이터를 전송한다.
   - SDK가 HTTP, DatabaseCall, AWS Call 등을 캡쳐한다.
2. X-Ray daemon Or X-Raw AWS Integration 설치
   - Machine에 직접 설치해야 한다. (X-Ray에 쓸 수 있는 IAM 권한(PUT) 도 부여해야한다.)
   - X-Ray Daemon은 UDP Packet Interceptor로, low-level packet을 통해 X-Ray에 데이터를 전송한다.
     - 20000/UDP 에서 동작하며, Raw Segment 데이터를 수집하여, 매 초마다(배치로) AWS-X-Ray에 API로 전달한다

### 세부 구성요소
- Trace: X-Ray에서 수집한 데이터의 집합 (Segments의 집합)
- Segments: X-Ray에서 수집한 데이터의 기본 단위
- SubSegments: Segment의 하위 단위 (Segment에 디테일 추가)
- Sampling: X-Ray에서 수집할 데이터의 비율을 설정하는 기능
  - 코드를 변경하지 않고, Rule을 통해 비율을 조정할 수 있다.
  - Reservoir: 1초동안 샘플링할 데이터의 수
  - Rate: Reservoir를 초과 한 이후 샘플링할 데이터의 비율 (0~1)
- Annotations: Segment에 정보를 추가하는 기능 
  - Key-Value 쌍으로 저장
  - **Indexing되어, 검색이 가능하다.**
- Metadata: Segment에 정보를 추가하는 기능
  - Key-Value 쌍으로 저장
  - **Indexing되지 않으며, 검색이 불가능하다.**

## CloudTrail
- AWS 계정의 API 호출을 기록하는 서비스
  - Console
  - SDK
  - CLI
  - AWS Services

### Cloud Trail Event
- Management Event
  - AWS Management Console, AWS CLI, AWS SDK를 통해 발생하는 API 호출을 기록한다.
  - AWS 서비스의 관리 작업을 기록한다. (ex. IAM, EC2, S3)
  - Read/Write API 호출을 모두 기록한다.
- Data Event
  - 기본적으로는 수집하지 않는다.
  - S3, Lambda, DynamoDB와 같은 AWS 서비스의 데이터 작업을 기록한다.
    - S3 Object-Level API 호출을 기록한다. (ex. GetObject, PutObject)
    - Lambda Function Invocation을 기록한다.
    - DynamoDB Table에 대한 API 호출을 기록한다.
- Cloud Trail Insight Event
  - 유료
  - 일종의 AuditLog
  - Management Event의 비정상적인 API 호출을 분석하고 기록한다.
    - 부정확한 리소스 프로비저닝
    - 서비스 제한 초과
    - AWS IAM 작업 폭주
- CloudTrailEvent는 기본적으로 90일의 보관기간을 가진다.
  - 장기보관 하려면 S3로 넘기고, Athena로 분석한다.



## ADOT (AWS Distro for OpenTelemetry)
- AWS에서 제공하는 OpenTelemetry 배포판
- OpenTelemetry는 애플리케이션의 메트릭, 로그 및 트레이스를 수집하고 전송하는 오픈 소스 프로젝트
- AWS Distro for OpenTelemetry는 AWS 서비스와 통합되어, AWS에서 제공하는 모니터링 및 로깅 서비스에서 사용할 수 있도록 지원