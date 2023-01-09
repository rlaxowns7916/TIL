# Rank 함수
- mysql 8버전 부터 사용가능
- rank, dense_rank 함수 사용가능


## Rank()
- rank() over (order by 정렬) 
- 정렬 Score가 같으면 같은 순위가 된다.
- 순위를 건너뛴다.
  - 1,2,2,4 ...
```sql
SELECT 
    id,
    amount,
    rank() over (order by desc ) as ranking
FROM
    ITEM;
```

## DenseRank()
- rank() over (order by 정렬)
- 정렬 Score가 같으면 같은 순위가 된다.
- 순위가 연속적이다.
    - 1,2,2,2,3
```sql
SELECT 
    id,
    amount,
    dense_rank() over (order by desc ) as ranking
FROM
    ITEM;
```

## Group별 Rank
- **partition by가 있으면 된다.**
```sql
SELECT 
    id,
    amount,
    rank() over (partition by category order by desc ) as ranking
FROM
    ITEM;
```