# SQL Optimizer
- SQL 최적화를 담당한다.
- 실행계획(Execution Plan) 을 세우며, 최적의 연산을 수행한다.
- 별도의 프로세스가 아니라, DB 서버 프로세스가 가지고 있는 기능이다.

## 종류

### [1] Rule-Based Optimizer
**미리 정해진 규칙에 따라서 실행계획을 결정한다.**   
**고정된 규칙을 따르기 떄문에 유연하지 못하다.**
- 연산자 우선순위
- 접근경로의 우선순위
- SQL문장의 syntax규칙

#### 왜쓰는가?
```text
1. 단순하다.
2. 단순한 만큼 CBO(Cost-Based-Optimizer) 같은 비용측정을 하지 않기떄문에 빠르다.
3. 간단한 시스템에서 성능이 우선시 될 때 사용하는 것이 좋다.
```
### [2] Cost-Based Optimizer
**여러 정보를 사용하여 가장 비용이 적게드는 실행계획을 결정한다.**
#### <1> 기본정보
- Table
- Column
- Index

#### <2> 오브젝트 통계
- Table 통계
- Index 통계
- Column 통계

#### <3> 시스템 통계
- CPU 속도
- Single Block I/O 속도
- Multi Block I/O 속도

#### <4> Optimizer Property
