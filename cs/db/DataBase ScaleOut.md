# Database ScaleOut
- 하나의 DB서버만 운영하는 것은 SPOF가 될 수 있다.
- DB또한 확장을 하여, HA를 보장해야 한다.

## 1. Cold-StandBy
- 수직적 확장이다.
- MainDB와 스펙, 설정은 동일해야 한다.
- MainDB가 장애가 날 것을 대비하여 다음 MainDB를 대기시켜놓는것이다.
- MainDB는 주기적으로 BackUp을 수행한다.
  - S3같은 Storage에 BackUp파일을 저장한다.
  - MainDB가 Down됐을 때, Down 되기 전 지점까지의 BackUp을 수행한다.
- DownTime이 증가한다. 
  - BackUp파일을 유휴서버가 읽어서 데이터 일관성을 유지해야 하기 때문이다.
- 데이터 유실이 허용 가능한 서비스여야 한다.
  - DownTime동안의 데이터는 유실되게 된다.

## 2. Warm-StandBy
- 수직적 확장이다.
- 대기중인 DB는 MainDB로 부터 지속적인 Replication를 수행한다.
- Replication의 시간 차에 따른 조금의 데이터 유실이 발생 할 수 있다.
  - Cold-StandBy보다는 훨씬 좋다.
- MainDB가 다운되면, 대기중인 서버로 트래픽을 리다이렉션 시키면 된다.
- Cold-StandBy에 비하면 훨씬 좋은 방법이다.
  - DownTime이 훨씬 적다.
  - 데이터 유실이 적다.


## 3. Hot-StandBy
- 수평적 확장이다.
- Client가 여러개의 분산된 DB에 쓰고 읽는다
- 부하가 분산되며, 하나의 Host가 죽어도 Active한 Host로 리다이렉션만 시키면된다.