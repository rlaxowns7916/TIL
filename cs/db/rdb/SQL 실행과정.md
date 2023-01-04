# SQL 실행과정

## [1] SQL 파싱
***SQL Parser가 사용자의 SQL구문을 받은 후 처음으로 파싱을 수행한다.***

### <1> 파싱 트리 생성
- 파싱트리는 SQL문을 Tree형태로 나타낸 AST (Abstract Syntax Tree)
- 구문의 전체적인 형태를 나타낸다.
- SQL 파서는 DBMS가 해당 명령문을 더욱 이해하기 쉽게만들며, 다양한 명령구문을 올바른 순서대로 실행하게 한다.
- 쿼리를 디버깅하거나 최적화하는데 유용하게 사용된다.

![스크린샷 2023-01-01 오후 8 58 58(2)](https://user-images.githubusercontent.com/57896918/210171318-c9c40995-d280-4805-b21f-7f9e8e3cab13.png)


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
- Row-Source Generator가 역할을 담당한다.
- SQL Optimizer가 선택한 실행 경로를 받아 Row Source Tree와 SQL 엔진이 실행 가능한 Binary Code를 생성한다.

### Row Source Tree
**SQL 실행의 방법들이 대부분 들어있다.**
- 테이블의 순서
- 테이블 Access 방법
- Join 방법
- Filter
- 정렬 방법




