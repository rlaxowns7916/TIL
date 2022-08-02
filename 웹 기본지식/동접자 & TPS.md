# 웹사이트 동접자 계산
- http는 ConnectionLess하다.
- 그렇기 때문에 동시접속자의 수를 정확하게 판단하기 어렵다.
- 보통 웹서버는 동시접속자보다는 TPS(Transaction Per Second)를 성능의 척도로 판단한다.


## 사용자의 유형

### 1. Concurrent User
- 트랜잭션을 발생 시킨 사용자
- 트랜잭션의 결과물을 Read하고 있는 경우도 포함된다.
- ActiveUser + InActiveUser 이다.

### 2. Active User
- 동시 사용자라고 불린다.
- 트랜잭션을 발생시키고 결과를 기다리는 사용자이다.
- Connection을 유지하고 있는 사용자도 포함된다.

### 3. InActive User
- 트랜잭션의 결과를 Read하고 있는 사용자이다.

## 대략적인 계측치
- GA (GoogleAnalytics)에서는 다음의 공식을 따른다.
- ConcurrentUser = (시간당 세션 + 평균 세션 시간(분)) / 60;


# TPS (Transaction Per Second)
- 초당 트랜잭션 수
- 서비스 성능의 척도가 된다.

## saturation point
- TPS가 증가하지 않는 지점
- TPS가 고정되며, ResponseTime이 증가한다.