# SQL I/O

## [1] Block 단위 I/O
- Block은 DBMS에서 제공하는 가장 작은 I/O의 단위이다.
- 특정 Record를 하나만 읽고싶어도, 해당 Block 전체를 읽게된다.

## [2] Block에 접근하는 방법

### <1> Sequential Access
- 논리적, 물리적 순서로 연결된 Block을 차례대로 읽는 것이다.
  - Index의 경우, Leaf Block 앞뒤로 연결이 되어 있다. (Double-LinkedList)
  - Table의 경우, 논리적인 연결고리를 갖고있지 않다.
    - **Table Full Scan**
      - Oracle의 경우, Segment에 할당된 Extent목록을 Segment Header에 Map 형태로 관리한다.
      - Extent의 첫번 째 Block의 주소값을 갖고있으며, 그 첫번째 Block부터 순차적으로 읽게 되는 것이다.
      - **한번에 여러개의 Block을 탐색하는 MultiBlock I/O 방식이다.**
- 많은 양의 데이터를 읽을 때는 Sequential Access가 더 효율적이다.

### <2> Random Access
- SingleBlock I/O 방식이다.
  - 한번에 하나의 Block만 탐색하게 된다.
  - MultiBlock I/O보다 I/O 횟수가 많기 때문에, 성능상으로 불리하다.
- Index 접근 후 확인한 ROWID를 이용하여 Table에 접근 할 경우 발생한다.
- 소량의 데이터에 접근하는 경우, 더 효율적이다.

## [3] 논리적 I/O vs 물리적 I/O

### 논리적 I/O
- 해당 SQL을 처리하는데 드는 총 블록 I/O를 말한다.
- Cache에 들리든, Disk에 들리든 총 블록 I/O의 갯수만을 생각한다.
- 논리적 I/O 자체를 줄이면 성능을 향상 시킬 수 있다.
  - 논리적 I/O가 준다면, BCH를 만족하지 않는, 물리적 I/O의 수치도 줄기 때문이다.
  - **논리적 I/O를 줄임으로 해서, 물리적 I/O를 줄이는 것이 SQL 튜닝의 핵심이다.**

### 물리적 I/O
- Disk에서 발생한 총 블록 I/O를 이야기한다.
- 논리적 I/O의 일부이다.
- 물리적 I/O만 줄이는 것은 튜닝을 불가능하다.
  - 메모리 증설을 통한 BufferCache영역의 크기를 늘릴 때만 가능하다.
  - 그렇기 떄문에 논리적 I/O를 줄여서, 절대적인 물리적 I/O의 수치를 줄이려는 것이다.

## [4] Single Block I/O vs MultiBlock I/O
- Disk에 I/O Call을 할 때, BufferCache에 적재하는 방법에 대한 차이이다.

### Single Block I/O
- 한번의 요청 당, 하나의 DataBlock씩 접근한다.
- 접근한 DataBlock을 하나하나 씩 BufferCache에 적재한다.

#### 언제 사용되는가?
1. Index Block
2. Table Block
**한번에 소수의 데이터에만 접근하기 떄문에, SingleBlock I/O가 더 적절하다**

### Multi Block I/O
- 한번의 요청 당, 여러개의 DataBlock에 접근한다.
- 여러개의 DataBlock을 한번에 BufferCache에 적재한다.
- MultiBlock I/O의 단위가 클 수록 좋다.
  - Process가 Blocking되는 시간이 줄어들기 때문이다.
  - Os: 1MB, Oracle: 8KB
- Disk I/O 발생 시, 인접한 Block들을 함꼐 적재한다.
  - 인접한 Block이란, 같은 Extent에 속한 Block을 의미한다.

#### 언제 사용되는가?
- Table Full Scan
**한번에 여러개의 데이터에 접근하기 떄문에, MultiBlock I/O가 더 적합하다.**