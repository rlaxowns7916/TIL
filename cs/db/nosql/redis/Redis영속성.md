# Redis 영속성

## [1] RDB (Redis DataBase)
- 특정한 시점에 Snapshot을 만드는 방식이다.
- Snapshot을 만든 후, Disk에 저장한다.
  - 다음 Snapshot을 준비하는 과정에서 장애가 발생하면, 데이터 유실이 발생 할 수 있다.
  - 장애 복구과정에서, snapshot 파일을 다시 사용하면 되기 때문에 복구가 빠르다.
  - snapshot을 Replica의 복제 데이터셋으로 사용하게 할 수 있다.
- Snapshot이 클 경우, 병목 현상이 발생 할 수 있다.
  - Disk에 저장하기 때문에, 충분한 공간을 확보해야 한다.
## RDB 설정
- redis.conf 파일에 저장되어있다.
- 기본 설정은 다음과 같다.
  - ```shell
        save 900 1       # 900초(15분) 동안 1개 이상의 키가 변경되었을 경우
        save 300 10      # 300초(5분) 동안 10개 이상의 키가 변경되었을 경우
        save 60 10000    # 60초(1분) 동안 10,000개 이상의 키가 변경되었을 경우
    ```
  - rdbcompression (yes)
    - RDB 파일을 저장하기 전에 데이터 압축을 사용 
    - 기본적으로 LZF 압축 알고리즘을 사용
  - rdbchecksum (yes)
    - RDB 파일 저장 시 체크섬을 사용하여 데이터 무결성을 검사
  - dbfilename (dump.rdb)
    - RDB 파일의 이름
  - dir (./)
    - RDB 파일과 AOF 파일이 저장될 디렉토리를 지정
    - 기본적으로 현재 디렉토리에 저장

## [2] AOF