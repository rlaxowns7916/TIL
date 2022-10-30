# UndoLog vs Redo Log vs Binary Log

## 사전 지식
### Buffer Pool
- InnoDB의 메모리 공간 (캐시)
    - Table Caching
    - Index Data Caching
- 메모리 공간이기 떄문에 장애 발생 시 휘발 될 수 있다.

***

## 1. Undo Log
- Rollback 시 UndoLog의 기록을 참조해서 복구한다.
- CheckPoint시에 Disk에 기록한다.
- 긴 트랜잭션은 성능에 영향을 미친다.
  - 지속적으로 파일에 기록이 되기 떄문에 무리가간다.
  - 긴 트랜잭션을 롤백하는 것도 큰 작업이다.
- **MVCC가 UndoLog를 통해서 구현된다.**
  - 여러개의 버전이 동시에 관리된다.
  - Lock 없이 Read하는 것이 목적이다.


## 2. Redo Log
- DB장애시 복구를 위해서 사용되는 로그이다.
  - 트랜잭션의 불완전한 완료를 복구하기 위해서 사용한다.
- 데이터 변경이 있을 시에만 Log에 기록된다.
  - DML (SELECT는 제외)
  - DDL
  - TCL
- MySQL 장애시, BufferPool에 저장되어 있던 데이터 유실을 방지하기 위해서 사용된다.

### Redo Log Buffer
- 우선 위의 대상들을 Redo Log Buffer에 저장시켜둔다.
- Redo Log Buffer도 메모리 영역이기 때문에, Disk(파일)로 저장시켜야한다.
- CheckPoint 이벤트가 발생하거나, 트랜잭션 커밋 시에 Disk에 기록한다.
- Redo Log Buffer에 있는 데이터가 Disk에 기록되지 못했다면 휘발된다.

### Redo Log File
- 2개의 Redo Log File로 구성된다.
- 1개의 File이 꽉 찼을 때, 다른 파일로 옮겨가서 작성하게 되는데 이떄 발생하는 이벤트가 **CheckPoint Event** 이다.
- 장애가 발생하면, Redo Log File을 읽어서 복구하는 것이다.

## 3. Binary Log
- mysql 의 로그 또는 Row내용을 주로 저장한다.
- Replication에 사용된다.