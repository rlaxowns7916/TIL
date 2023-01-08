# [1] Library Cache
- SGA의 구성요소이며, 메모리 공간이다.
- SQL의 일련된 처리 이후 (파싱 -> 최적화 -> Row Source 생성)을 거쳐서 생성한 것들을 캐싱해 두는 것이다.
- SQL Parser의 파싱이후, 먼저 Library Cache에 들려서 Caching되어 있는 것이 있는지 확인한다.
  - **SQL Text의 Hash 값을 통해서 존재 여부를 확인한다.**
  - SQL 최적화 연산을 최소화 하기 위함이다.
  - Library Cache에 존재한다면, 그 이후의 과정(최적화 -> Row Source 생성) 은 생략 된다.
- 여러가지가 저장된다. (Data를 저장하는 것이 아닌 그 외의 것을 저장하기 때문에 Code Cache라고 불린다.)
  - Procedure 의사코드
  - 구문분석 트리
  - 실행 계획
- **SQL문 그자체가 아닌, DBMS가 쉽게 이해하고 실행할 수 있는, 파싱 된 SQL문이 포함된다.**



## 소프트 파싱 vs 하드 파싱
- 소프트 파싱은 Library Cache에서 CacheHit 이후 추후의 작업 (최적화 -> Row Source 생성)을 하지 않고 바로 실행단계로 넘어간다.
- 하드 파싱은 Library Cache를 CacheMiss 이후 추후의 작업을 모두 실행 한 후 실행단계로 넘어가는 것이다.
    - 최적화 연산은 CPU를 많이사용하는 무거운 작업이기 때문에 하드파싱이라고 불린다.

 ## 바인드 변수 (Bind Variables)
- 반대의 의미는 리터럴 변수 (Literal Variablds) 이다.
  - 자주 변경되는 변수의 값이 하드코딩 된 것이다.
  - 하드코딩을 자주 발생시키는 요인이 된다.
- 이 방법을 통해 하드 파싱은 한번만 일어나면서, Library Cache를 거치는 소프트파싱의 이점을 누릴 수 있다.
  - **바인드 변수의 구문이 Library Cache에 저장되지는 않는다.**
  - **바인드 변수의 실행계획이 저장된다.**
  - Bind Variables의 이전 실행계획 재사용을 통해서 성능을 향상 시키는 것이다.
- 명령문 작성 시점에 어떠한 값이 들어갈지 표현하지 않는다.
- DBMS마다 문법이 다르다.
  - Oracle: :id
  - MySQL: @id

### Bind Variable 선언

#### mysql
```sql
SET @id = 1;

SELECT 
    * 
FROM 
    employees 
WHERE 
    id = @id;
```

#### oracle
```sql
-- Declare a bind variable
VAR id NUMBER;

-- Assign a value to the bind variable
EXECUTE :id := 1;

-- Use the bind variable in a SELECT statement
SELECT name, age FROM users WHERE id = :id;
```

### PreparedStatement와 차이점은?
```text

[1] 문법
- 바인드 변수는 주로 자리 표시자로 표현된다.
- PreparedStatement는 재사용을 목적으로 미리 템플릿 형태로 만들어지며, 실행 할 때 매개변수를 제공한다.

[2] 성능 향상
- 바인드 변수는 데이터베이스 서버가 다른 리터럴 값으로 이전에 실행된 SQL 문에 대한 실행 계획을 재사용할 수 있도록 하여 성능을 향상시키는 데 도움이 될 수 있다. 
- PreparedStatement는 SQL 문에 대한 실행 계획을 최적화하고 재사용을 위해 캐시할 수 있도록 하여 성능을 향상시킬 수 있다.



자리표시자를 사용하고, 별도로 값을 지정해준다는 기본 개념에서는 동일하다.
DBMS의 성능향상 측면에서도 동일하다.
```

# [2] DB Buffer Cache
- 실제 Data를 저장하는 Cache이다. (Library Cache는 실행계획, Procedure등을 캐싱하는 Code Cache 라고 불린다.)
- **Disk에서 읽은, DataBlock을 Caching하여 Disk I/O를 줄이는 것이 목적이다.**
- DBMS는 DataFile에 접근 전, BufferCache를 먼저 탐색하게 된다.
  - 순서: Library Cache(SQL 실행에 필요한 실행 계획 등 기타정보 얻기) -> Buffer Cache (DataBlock Cache)
- **BufferCache에 없다면, Disk에서 DataBlock을 찾고, BuuferCache에 갱신 후 Return 한다.**
- **Disk I/O 동안 DBMS Process는 Waiting Queue에서 대기한다.**
- Direct Path I/O를 제외한 모든 Disk I/O(Block I/O)는 Buffer Cache를 거친다.

## Buffer Cache Hit
- 트랜잭션이 잦은 어플리케이션의 경우 99%의 Cache Hit를 목표로 해야한다.
- 튜닝을 제대로 할시에, 불가능한 수치가 아니다.
```text
BCHR (Buffer Cache Hit Ratio)

= (BufferCache에서 찾은 Block 수 / 총 읽은 Block 수 ) * 100 
= ((논리적 I/O - 물리적 I/O) / 논리적 I/O) * 100
= ((1 - 물리적 I/O) / 논리적 I/O) * 100)
```
- BCHR이 높을 수록 성능이 좋다.
  - 하지만 이 것이 효율적인 Query라는 증거는 없다.

## Buffer Cache를 거치는 연산
**Index, Table Block 접근 시에 BufferCache에 먼저 들려본다고 생각하면 편하다.**
1. Index Root Block을 읽을 때
2. Index Branch Block을 읽을 때
3. Index Leaf Block을 읽을 때
4. Table Block을 읽을 때
5. Table Full Scan 할 때

## Buffer Cache 구조
- Hash 구조로 관리한다.
  - 같은 Input은 항상 같은 HashBucket (= Hash Chain)에 등록된다.
  - Bucket내에서 정렬을 보장되지 않는다.

## Cache Buffer Latch
- 일종의 Lock 개념이다.
  - **해당 Block을 Chain에서 찾았다면, Latch를 해제하여 다른 Process가 탐색 할 수 있게해야 한다.** (Latch는 Block의 수정까지 Lock을 잡고 있는 것이 아니다.)
  - Latch를 풀고, 데이터를 수정 할 떄 동시에 데이터 수정이 된다면 데이터 정합성에 문제가 생길 수 있다.
    - 이런 문제를 해결하기 위해서 Oracle에서는 BufferLock을 제공한다.
- Cache에 동시에 N개의 프로세스가 접근 하는 것을 막는 것이다.
  - Cache에 접근해 있는 상태에서, Cache의 변경이 일어난다면 정합성에 문제가 생기게된다.
- 다양한 Latch가 존재한다.
  - Cache Buffer Chain Latch
  - Cache Buffer LRU Chain Latch
- **DBMS의 성능을 높이기 위해서는, Latch 경합 또한 줄여야 한다.** 
  - 그러기 위해서는 논리적 I/O의 절대적인 수치를 줄이는 것이 필수적이다.