# SQL 실행과정

## [1] SQL 파싱
***SQL Parser가 사용자의 SQL구문을 받은 후 처음으로 파싱을 수행한다.***

### <1> 파싱 트리 생성
- 파싱트리는 SQL문을 Tree형태로 나타낸 AST (Abstract Syntax Tree)
- 구문의 전체적인 형태를 나타낸다.
- SQL 파서는 DBMS가 해당 명령문을 더욱 이해하기 쉽게만들며, 다양한 명령구문을 올바른 순서대로 실행하게 한다.
- 쿼리를 디버깅하거나 최적화하는데 유용하게 사용된다.

### <2> Syntax 체크
- 문법적 오류가 있는지 확인한다.
- 사용 불가능하거나, 누락된 키워드가 있는지 확인한다.

### <3> Semantic 체크
- 의미상의 오류가 없는지를 확인한다.
- 존재하지않는 Table, Column을 참조하거나 권한을 갖고있는지 확인한다.

## [2] SQL 최적화
- **SQL Parsing 이후 SQL Optimizer가 해당 역할을 수행한다.**
- DBMS의 성능을 높이는 가장 중요한 엔진이다.
- 다양한 정보를 통해서 SQL Optimizer가 최적의 경로를 찾는다.
  - 기본정보: Table, Column, Index
  - 오브젝트 통계: Table 통계, Column 통계, Index 통계
  - 시스템 통계: CPU 속도, SingleBlock I/O, MultiBlock I/O
  - Optimizer 파라미터

## [3] Row-Source 생성
- SQL Optimizer가 선택한 실행 경로를, **Code 또는 Procedure** 형태로 포맷팅하는 단계이다.
- Row-Source Generator가 해당 역할을 맡는다.



## 소프트 파싱 vs 하드 파싱
- 소프트 파싱은 Library Cache에서 CacheHit 이후 추후의 작업 (최적화 -> Row Source 생성)을 하지 않고 바로 실행단계로 넘어간다.
- 하드 파싱은 Library Cache를 CacheMiss 이후 추후의 작업을 모두 실행 한 후 실행단계로 넘어가는 것이다.
  - 최적화 연산은 CPU를 많이사용하는 무거운 작업이기 때문에 하드파싱이라고 불린다.

### Library Cache 
- SGA의 구성요소이며, 메모리 공간이다.
- SQL의 일련된 처리 이후 (파싱 -> 최적화 -> Row Source 생성)을 거쳐서 생성한 내부 프로시저를 캐싱해 두는 것이다.
- SQL Parser의 파싱이후, 먼저 Library Cache에 들려서 Caching되어 있는 것이 있는지 확인한다.
  - SQL 최적화 연산을 최소화 하기 위함이다. 
  - Library Cache에 존재한다면, 그 이후의 과정(최적화 -> Row Source 생성) 은 생략 된다.
