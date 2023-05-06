# Bulk
- 대용량의 작업을 여러개로 묶은 후 에 처리하는 것이다.
- 성능향상이 크게 일어난다.
- DB의 작업소요를 최소화 하는 것이 핵심이다.

## Bulk Insert
- 특정 묶음 단위로 한번에 대량의 데이터 Insert
- Transaction, Index등 기타 설정들을 조정하고 하는 것이 성능에 더 좋다.

1. 기존
```sql
INSERT INTO COMMENT(id,desrciption,user_id,created_at,is_delete) VALUES(1,"Hello",1,now(),false);
INSERT INTO COMMENT(id,desrciption,user_id,created_at,is_delete) VALUES(2,"Hello",2,now(),false);
INSERT INTO COMMENT(id,desrciption,user_id,created_at,is_delete) VALUES(3,"Hello",3,now(),false);
INSERT INTO COMMENT(id,desrciption,user_id,created_at,is_delete) VALUES(4,"Hello",4,now(),false);
INSERT INTO COMMENT(id,desrciption,user_id,created_at,is_delete) VALUES(5,"Hello",5,now(),false);
```

2. Bulk
```sql
INSERT INTO COMMENT(id,desrciption,userId,createdAt) VALUES(1,"Hello",1,now()), (2,"Hello",2,now()),(3,"Hello",3,now()),(4,"Hello",4,now()),(5,"Hello",5,now());
```


## Bulk Update
- 특정 묶음 단위로 한번에 대량의 데이터를 Update 하는 것이다.

### 순서 (MySQL, Maria DB 기준)
(임시 테이블 없이 원본에서 쪼개서 해도 될 것 같다.)
1. 임시 테이블 생성
2. 임시테이블에 Bulk Insert
3. 원본테이블과 임시테이블 JOIN 후 BulkUpdate
4. 임시 테이블 삭제

```sql
DROP TABLE TEMP_COMMENT; # 기존 임시테이블 삭제

CREATE TEMPORARY TABLE TEMP_COMMENT LIKE COMMENT; # 원본 테이블과 똑같이 임시 테이블 생성

INSERT INTO TEMP_COMMENT VALUES(SELECT * FROM COMMENT 
                                WHERE user_id < 10 
                                LIMIT 1000
                                OFFSET ?
                                ORDER BY ID);

UPDATE COMMENT c JOIN TEMP_COMMENT tc ON c.id = tc.id SET c.isDelete = true; 

```



## 성능향상이 일어나는 이유
- 네트워크 비용의 감소
- 데이터의 Insert, Update에는 추가적인 작업이 필요하다.
  - Transaction
  - Index
- I/O 작업을 한번에 처리
- 이 작업을 개별 쿼리 당 실행이아닌, 묶음 단위로 실행하기 때문에 성능 개선이 일어나는 것이다.
  - Bulk (x): ((N * 쿼리 개별 소모 시간) * (N * 쿼리 전후 추가 작업 시간))
  - Bulk (o): ((N * 쿼리 개별 소모 시간) * ((N/Bulk단위) * 쿼리 전후 추가 작업 시간))