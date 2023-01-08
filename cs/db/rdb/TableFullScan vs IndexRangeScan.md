# Table Full Scan vs Index Range Scan
- Table에 저장된 Data를 읽는 방식이다.
- Index를 타느냐 타지 않느냐 여부이다.
  - Index를 타게 하는 것이, 모든 성능문제를 해결하는 방법이 아니다.

## [1] Table Full Scan
- 통계성 데이터 추출 (Batch)등의 대용량 데이터 작업에 유리하다.
- Sequential Access + MultiBlock I/O 방식이다.
- Index를 사용하지 않는다.


## [2] Index Range Scan
- Index를 거친 후, 테이블에서 데이터를 찾는다.
  - 소량의 Data를 찾을 때에 사용하자.
- Random Access + SingleBlock I/O 방식이다.
- **많은 데이터를 읽어야 할 때는, 오히려 더 성능을 낮추는 원인이된다.**
  - BufferCache에서 Block을 못찾으면, 하나의 Block을 얻기위해서 한번의 Disk I/O가 발생하는 것이다.
  - 논리적 I/O의 절대적 수치 또한 늘어나는 결과를 만들어낸다.