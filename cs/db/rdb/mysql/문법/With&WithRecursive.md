# With
- 임시테이블을 생성한다.
- mysql 8.0 이전버전에서는 동작하지 않는다.
```sql
WITH [Table 명] AS (
    SELECT [Column 명]
    FROM [Table 명]
    ...
)
```

# With Recursive
- 반복적인 작업을 하는 구문이다.
- 임시적인 Table을 생성해 낸다.

## [1] 문법
```mysql
WITH RECURSIVE [Table 명] AS (
            SELECT [초기 값] AS [Column 명]
            UNION ALL
            SELECT [값 조건] FROM [Table 명] WHERE [재귀 종료 조건]
        )
```


## [2] 예시
```mysql
SELECT
    h.num as HOUR,
    COUNT(ao.ANIMAL_ID) as COUNT
FROM
    ANIMAL_OUTS as ao
        RIGHT OUTER JOIN(
        WITH RECURSIVE hours AS(
            SELECT 0 AS num
            UNION ALL
            SELECT num+1 FROM hours WHERE num < 23
        )
        SELECT num FROM hours
    )as h
ON
    h.num = HOUR(ao.DATETIME)
GROUP BY
    h.num
```