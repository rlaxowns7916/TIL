# DB 가용을 위한 기술

# Clustering
- 여러대의 Server가 하나의 Storage를 공유한다.
- 시스템 가용성을 높이는 방법이다.

## [1] Active - Active Clustering
- 2개의 Instance가 하나의 Storage를 바라보는 것이다.
  - Write성능을 높일 수 있다.
- 하나의 Instance가 죽어도 정상적으로 동작을 수행가능하다.
- **Storage는 하나기 때문에, 병목현상이 발생 할 수 있다.**
- 동시성문제가 발생 할 수 있다.
   - 하나의 Storage를 사용하는 만큼, 동시에 같은 것을 수정하려 할 수 있다.
   - Lock, 트랜잭션관리, 동시성 제어 같은 기술이 필요하다.
   - 일부 Clustering Solution은 이를 지원해주지만, 지원하지 않는 경우 직접 구현해야 할 수 있다.
   - 
## [2] Active - Standby Clustering
- Active Instance에 문제가 생겼을 때, StandBy를 Active로 전환한다.
  - **StandBy가 Active로 전환되는 시간동안에는 서비스가 동작하지 않을 수 있다.**
- Active-Active방식과 비용적인 측면에서는 차이가 없을 수 있으나, 병목현상을 해결 할 수 있다.

# Replication
- Server와 Storage 모두 독립적으로 구성하는 것이다.

## [1] Master - Slave
- 한개의 Master와 N개의 Slave로 구성된다.
- Master에서 Write를 수행하고, Slave에서 Read를 수행한다.
  - Write와 Read작업을 분리함으로써, 부하를 분산 할 수 있다.
  - Write작업이 많은 경우, 적합하지 않은 구조일 수 있다.
- **Master와 Slave는 Replication을 통해서 동기화 된다.**
- Master가 죽었을 경우, Slave 중 하나가 Master로 격상된다.
  - Master가 죽고, Slave를 격상 시킬 때 **동기화 문제가 발생 할 수 있다.**
  - **물론 Slave 사이에도 데이터가 일관적이지 않을 수 있다.**

## [2] Master - Master
- 두개이상의 Master DB가 동기화되면서 작업을 수행한다.
  - 각각의 Master DB는 Read와 Write를 동시에 수행한다.
  - 동기화에는 주로 **Sync-Replication**을 사용한다. (Master 사이의 일관성을 유지하기 위헤서)
- Write작업도 부하분산이 되기 때문에, 시스템의 전체적인 부하를 분산 할 수 있다.
- 구성에 복잡성이 증가한다.
- **충돌 문제가 발생할 수 있다.**
  - 두개의 Master DB가 동시에 같은 데이터를 변경 할 때, 문제가 발생 할 수 있다.
 
    
## 복제 동기화 시점
### [1] Asnyc - Replication
- 동기화가 별도로 이루어진다.
- 일정 시점마다 동기화가 이루어진다.
  - 동기화가 이루어지기 전에, Instance에 문제가 생긴다면 정합성 문제가 발생 할 수 있다.
- 동기화가 이루어지지않아도, Write작업을 수행한다.
- 주로 Master - Slave 구조에서 자주 사용된다.

### [2] Sync - Replication
- 동기화가 Write 이후에 바로 이루어진다.
- 주로 Master - Master 구조에서 자주 사용된다.