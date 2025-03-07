# 공동 책임모델
- IAM은 사용자의 책임이다. 
  - Users, 
  - Groups, 
  - Roles, 
  - Policies,
  - Management
  - Monitoring
  - Key Rotation
  - ...
- 그 외의 Infra, 설정, 취약점 분석, 컴플라이언스 준수 등은 AWS의 책임이다.

# IAMUser (Identity And AccessManagement User)
- RootAccount는 계정이 생성될 떄 자동으로 만들어진다.
  - 사용되어서는 안된다.
  - RootAccount에 대해서는 MFA 적용이 권장된다.
    - APP, SecurityKey, H/W TOTP Token 방식이 있다.
- 조직에서, 한사람마다 IAM을 부여할 수 있고, Grouping 할 수 있다.
  - Group을 통해서 권한을 제어하는 것이 일반적이다. 
    - User에게 직접적으로 부여 할 수도 있다.
  - Group에는 사용자만 존재할 수 있고, Group간 계층구조 적용은 불가능하다.
  - Group에 속하지 않은 사용자도 존재 할 수 있다.
- Global 서비스이며, Region이 영향을 끼치지 않는다.
- **User는 여러 Group과 직접적인 권한설정을 받을 수 있으며, 모든 합집합이 해당 User의 권한이다.**
  - **Effect에서는 Deny가 우선된다.**

## IAM Policy Document
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "1",
      "Effect": "Allow",
      "Action": [
        "s3:ListBucket",
        "s3:GetObject"
      ],
      "Principal": {
        "AWS": "arn:aws:iam::123456789012:user/example-user"
      },
      "Resource": "arn:aws:s3:::example-bucket",
      "Condition": {
        "IpAddress": {
          "aws:SourceIp": "192.168.1.0/24"
        }
      }
    }
  ]
}
```
- Json 형식으로 구성된다.
- Version(Required): Policy언어의 버전을 지정한다.
- Statement(Required): 하나이상의 Poilcy를 정의하는 배열
  - Sid (Optional): Statement를 정의하는 identifier
  - Effect(Required): Statement 내부요소 (Allow/Deny)
  - Action(Required): 허용 또는 거부할 Service를 명시한다.
  - Resource(Required): 정책이 적 용될 AWS Resource를 지정한다. 
  - Principal(Optional): Policy에 적용될 주체 (User, Group, Role)를 지정한다.
  - Condition(Optional): 특정 조건이 만족 될 때만 실행하도록 명시한다.

# IAM Role (Identity And AccessManagement Role)
- AWS Service, 또는 어플리케이션이 특정 작업을 수행할 수 있도록 부여하는 역할(Role)
- AWS 서비스 (EC2, Lambda) 또는 외부사용자애게 부여가능
  - ex) EC2에서 S3 접근, Lambda에서 DynamoDB 접근
  - Ec2 Instance에 aws configure를 통해서 정보를 입력하는 것이 아닌 IAM Role을 주어야한다.
- **Service가 AssumeRole을 사용하여 역할을 위임받아 인증하는 것**

# IAM 보안 도구
- IAM Credential Report
  - AWS 계정에 대한 IAM 사용자 보안 현황을 알 수 있음
  - CSV파일의 형태를 제공하며, 아래와 같은 데이터가 있다.
    - user
    - arn
    - password 관련정보
    - mfa 관련 정보
    - accessKey 관련 정보
- IAM Access Advisor
  - 특정 IAM 사용자의 AWS 서비스 접근기록을 볼 수 있다.
  - Role을 통해 부여된 권한과 실제 사용권한을 비교하여, 불필요한 권한을 회수 가능하다. (최소 권한 유지)
  