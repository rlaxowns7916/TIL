# Trigger
- 특정 테이블에 INSERT, SELECT, UPDATE, DELETE 문 등이 실행되면 데이터베이스에서 자동으로 동작하는 프로그램
  - Trigger Event는 Insert,Update,Delete 중 하나 이상이 올 수 있다.
- 사용자가 직접 호출하는 것이 아닌, 데이터베이스가 자동적으로 호출하는 것이 핵심이다.
- 트랜잭션에 포함된다.
  - 롤백이 가능하다.
- OLD/NEW 키워드로 변경 이전, 변경 이후 테이블의 데이터에 접근 가능하다.

## Trigger 선언
```sql
[트리거 생성 OR 수정] TRIGGER 트리거명 [트리거 조건]
ON [Table 명 | View 명]
WHEN [조건]
begin 
    [Trigger Body]
end;
```

### 트리거 생성 OR 수정
- CREATE
- CREATE OR REPLACE
  - 이미 존재하는 트리거가 있다면 Override

### 트리거 조건
- BEFORE, AFTER
  - INSERT
  - UPDATE
  - DELETE
### 트리거 종류
- FOR EACH ROW : 행 트리거 
- 생략(default) : 트랜잭션 동안 한번만 수행되는 트리거