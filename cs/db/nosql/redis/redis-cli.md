# Redis-Cli
- CLI환경에서 Redis-Server에 접근 가능한 툴
  - Redis-Server가 구동되고있지않으면 당연히 명령을 실행 할 수 없다.
- 명령어를 통해서 Redis가 지원하는 동작을 수행할 수 있다.
  - https://redis.io/commands/


## 접속
```shell
$ redis-cli
```

## Info
```shell
$ info [Section]
```
1. Server
2. Clients
3. Memory
4. Persistence
5. Stats
6. Replication
7. CPU
8. Modules
9. ErrorStats
10. Cluster
11. KeySpace
- 생략시에 모든 Section이 노출된다.

## quit

## ping
```shell
$ ping [문자열]
```
- Redis-Server가 제대로 구동되어있는지 확인하는 방법
- 문자열이 생략되면 PONG, 문자열을 입력하면 문자열 그대로 리턴해준다.