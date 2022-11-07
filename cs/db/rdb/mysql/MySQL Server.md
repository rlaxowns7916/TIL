# MySQL Server

## [1] QueryCache (8.0 버전 이전)
- Query 분석 전 Cache에 존재하는지 확인한다. 
- Cache에 데이터가 존재한다면 그 뒤의 과정은 무시하고 바로 결과를 리턴한다.

### 왜 8.0부터 사라졌는가?
- Cache 관리가 오히려 성능 저하를 유발했다.
- 데이터 변경에 따른 Cache데이터 변경에 따른 동시성 Lock등이 문제였다.
- Oracle의 경우 쿼리캐시가 존재한다. 
  - 데이터가 아닌 실행계획 까지만이다.

***

## [2] Query Parser
- SQL이 문법상의 오류가 있지는 않은지 검증한다.
- Programming Language같이, Compile과 같은 작업을 할 수 없기 떄문에, 그때마다 확인해야 한다.
- Parsing과정을 통해 **SQL SyntaxTree**를 생성한다.

***

## [3] PreProcessor
- QueryParser의 Parsing단계에서 문제가 없다면 추가적인 분석을 실행한다.
- SQL문이 실제로 작동 가능한지 확인한다.
  - SQL의 실행권한
  - Table명이나, Column명 확인 
  - ...

***

## [4] Query Optimizer
- Query를 처리하기위한 최적의 방법을 산출해낸다.
  - 비용정보, 테이블의 통계정보등을 통해서 산출한다.
- 테이블 순서, 불필요한 조건 제거 등을 통해서 전략을 결정한다.
- Optimizesr의 전략에 따라서 성능이 달라진다.
- **Optimizer가 항상 최적의 전략을 선택하는 것은 아니며, 개발자가 QueryHint를 제공함으로써 성능을 더욱 높일 수 있다.**

***

## [5] Query Execute Engine
- StorageEngine에 HandlerAPI를 던진다.
- Query Optimizer 이 생성한 전략에 따라서 Query를 실행하는 역할이다.

