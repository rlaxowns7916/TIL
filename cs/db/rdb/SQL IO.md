# SQL I/O

## [1] Block 단위 I/O
- Block은 DBMS에서 제공하는 가장 작은 I/O의 단위이다.
- 특정 Record를 하나만 읽고싶어도, 해당 Block 전체를 읽게된다.

## [2] Block에 접근하는 방법

### <1> Sequential Access
- 논리적, 물리적 순서로 연결된 Block을 차례대로 읽는 것이다.
  - Index의 경우, Leaf Block 앞뒤로 연결이 되어 있다. (Double-LinkedList)
  - Table의 경우, 논리적인 연결고리를 갖고있지 않다.
    - **Full Table Scan**
      - Oracle의 경우, Segment에 할당된 Extent목록을 Segment Header에 Map 형태로 관리한다.
      - Extent의 첫번 째 Block의 주소값을 갖고있으며, 그 첫번째 Block부터 순차적으로 읽게 되는 것이다.
      - **한번에 여러개의 Block을 탐색하는 MultiBlock I/O 방식이다.**

### <2> Random Access
- SingleBlock I/O 방식이다.
  - 한번에 하나의 Block만 탐색하게 된다.
  - MultiBlock I/O보다 I/O 횟수가 많기 때문에, 성능상으로 불리하다.
- Index 접근 후 확인한 ROWID를 이용하여 Table에 접근 할 경우 발생한다.

## [2] DB Buffer Cache
- 실제 Data를 저장하는 Cache이다. (Library Cache는 실행계획, Procedure등을 캐싱하는 Code Cache 라고 불린다.)
- **Disk에서 읽은, DataBlock을 Caching하여 Disk I/O를 줄이는 것이 목적이다.**
- DBMS는 DataFile에 접근 전, BufferCache를 먼저 탐색하게 된다.

## [3] 논리적 I/O vs 물리적 I/O