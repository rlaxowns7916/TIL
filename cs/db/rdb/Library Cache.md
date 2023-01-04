# Library Cache
- SGA의 구성요소이며, 메모리 공간이다.
- SQL의 일련된 처리 이후 (파싱 -> 최적화 -> Row Source 생성)을 거쳐서 생성한 것들을 캐싱해 두는 것이다.
- SQL Parser의 파싱이후, 먼저 Library Cache에 들려서 Caching되어 있는 것이 있는지 확인한다.
  - **SQL Text의 Hash 값을 통해서 존재 여부를 확인한다.**
  - SQL 최적화 연산을 최소화 하기 위함이다.
  - Library Cache에 존재한다면, 그 이후의 과정(최적화 -> Row Source 생성) 은 생략 된다.
- 여러가지가 저장된다.
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