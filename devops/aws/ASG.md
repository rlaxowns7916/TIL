# ASG (Auto Scaling Group)
- 조건(Cloud watch) 에 따라 Scale-Out, Scale-In을 자동화한다.
- 최소와 최대치 EC2인스턴스의 Running을 보장한다.
- unhealthy한 instance가 있다면 종료시키고 새롭게 생성한다.
- ASG가 직접 생성한 Instance만 관리한다.
  - ASG가 Scale-In한 Instance를 명시적으로 다시 실행 시켜도, ASG에 영향을 줄 수 없다.

## AutoScaling Policy
### [1] Dynamic Scaling
- TargetTracking Scaling
  - 특정 지표를 조건에 맞추어서 처리 
  - CPU 40% 근처에 위치하게 하고싶다.
- Simple / Step Scaling (with CloudWatch)
  - ex) CPU의 Usage가 70%를 넘어서면 2개의 Instance를 추가해라
  - ex) CPU의 Usage가 30% 아래에 있으면 1개의 Instance를 삭제해라

### [2] Scheduled Scaling
- 사용패턴을 예측 할 수 있을 때 사용한다.
- ex) 매주 금요알 5시에 이벤트를 진행하니, 매주 금요일 5시~6시까지만 Instnace를 5개 추가해라

## ASG의 기준으로 잡기 좋은 지표
- CPU Utilization
- RequestCountPerTarget
- Average Network In/Out

## Scaling CoolDown
- Instance를 추가하거나 삭제한 후, 시스템 안정성을 위해서 대기하는 시간
- Scaling 직후 지표가 안정되지 않았는데 추가적인 동작을 수행할 수 있기 때문이다.

## Instance Refresh
- ASG가 관리하는 Instnace를 순차적으로 모두 교체하는 기능
  - 하나씩 종료하고 새로 띄우는 방식을 사용한다.
- 최신 AMI 적용, UserData변경 적용, EC2 패치 적용 등이 목적이다.