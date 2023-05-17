# ECS (Elastic Container Service)
- Docker Container를 AWS에서 실행
  - (=ECS Task를 ECS Cluster에서 실행한다.)

## ESC Task
- 설정에 맞게 Instance화 하는 것
### [1] EC2
- 여러개의 Ec2가 Clustering 된 것을, ECS Cluster라고 한다.
- 각 Instance가 ECS Agent를 내부적으로 실행해야 한다.
- ECS Task를 Launch하면, 각 Instance에 Docker Container를 배포한다.

### [2] Fargate
- 관리할 인프라가 없다.
  - EC2 Instance를 필요로 하지 않는다.
  - 모두 Serverless 이다.
- 그냥 ECS Task를 정의하면된다.
  - AWS는 내가 정의한 CPU/RAM에 맞게 알아서 실행한다.

## ECS Task IAM
### [1] EC2 Instance Profile 
- ECS Agent에 의해서 사용된다.
- ECS에게 API Calldmf qhsosek.
- Container의 log를 CloudWatch로 전송한다.

### [2] ECS Task Role
- 각 ECS Task에 역항을 정해줄 수 있다.
  - 각각의 ECS Task마다, 다른 Service를 실행 가능하다.
- TaskDefinition에서 사용된다.

## LoadBalancer
- ALB(Application LoadBalancer)로 지원된다.
- NLB(Network LoadBalancer)의 경우에는 높은 Throughput이나, 성능이 요구될 때 사용한다.
- ELB(Elastic LoadBalancer)의 경우에는 추천되지 않는다.
  - Fargate에서 지원안됨

## EFS - Data Volumes
- FileSystem을 ECS Task들에 마운트 하는 것이다.
- EC2유형, Fargate유횽 모두 호환된다.
- Serverless이기 떄문에, 인프라를 관리하지 않아도 된다.