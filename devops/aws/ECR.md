# ECR



## CLI로 Image pull

### [1] awscli 설치
```shell
$ brew install awscli     # 설치 커멘드
$ aws --version           # 설치 확인
```

### [2] AWS 인증
```shell
$ aws configure           # accessKey와 secretKey를 필요로한다.
```

### [2] ECR 인증
```shell
$ aws ecr get-login-password --region [region] | docker login --username AWS --password-stdin [account_id].dkr.ecr.[region].amazonaws.com
# account_id는 계정 12짜리 식별자 
```

### [3] Pull
```shell
$ docker pull [account_id].dkr.ecr.ap-northeast-2.amazonaws.com/[repository name:tag]
```
