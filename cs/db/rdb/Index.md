# Index
**RDBMS에서 검색속도를 높이기위한 기술**
- Index파일을 만들어서 따로 저장 
  - 추가공간이 필요하다. (DB 용량의 약 10%) 
  - 데이터와 데이터의 위치를 저장한다.
- Table FullScan이아니라, 인덱스파일을 보고 빠르게 검색
- CUD시의 성능저하가 발생한다.
  - CREATE - 데이터에 맞는 Index 생성
  - DELETE - Index를 사용하지 않는다는 작업을 수행한다.
    - Index에는 Delete 개념이 없다.
  - UPDATE - Index를 사용하지 않는다는 작업을 수행 후, 새로운 Index를 매핑한다.
- 분포도(Selectivity)가 중요하다
  - 분포도는 데이터가 테이블에 평균적으로 분포되어 있는 정보를 의미한다.
  - 분포도가 좋다는 것은 **중복이 없다 (== 분포도가 낮다)**는 것이다.    

## 복합 인덱스
- 순서가 중요하다.
- 자주 사용하는 것을 선행 칼럼으로 정의한다.
- 등치조건(=)으로 사용하는 것을 선행 칼럼으로 정의한다.
- 분포도 (Selectivity)가 좋은 칼럼을 선행 칼럼으로 정의한다.

## 인덱스 알고리즘
- HashTable은 O(1)인데 왜 B+Tree, B-Tree를 사용하는가?
  - 단일 접근은 HashTable이 빠르다.
  - 부등호 쿼리 (범위)에서는 HashTable은 정렬이되어있지 않기 떄문에 더 오래걸린다.

### 1. B+ Tree
- B-Tree의 확장모델
  - 당연히 정렬되어있다.
- IndexNode와 LeafNode로 구성
  - BranchNode에 데이터를 저장하지 않기 때문에, 더 많은 포인터 저장 가능 (전체적인 depth가 낮아짐) 
- LeafNode에 데이터가 저장됨 (다른 노드들에 데이터가 저장안되기 떄문에 메모리 효율성)
- LeaftNode끼리는 LinkedList로 연결
- 트리의 높이가 낮아진다.
- FullScan시 선형검색

#### cf) B-tree
- BinaryTree의 확장
  - 당연히 정렬되어있다.
- RootNode, BranchNode, LeafNode로 구성된다.
  - BranchNode와 LeaftNode에 데이터 저장 가능 
  - BranchNode에 데이터를 저장하기 때문에 B+Tree에 비해 Depth가 깊어질 수 있으나,
    자주 사용하는 데이터라면 rootNode에 가까이 둘 수 있어 성능상의 이점이 있음
- 한 노드당 2개 이상의 자식이 가능
- 어떠한 값에 대해서도 동일한 접근시간  == 리프노드를 같은 높이에 (균일성)
  - 삽입,삭제의 경우에 동적으로 균일성 유지
  - 불균형 트리 최악 시간복잡도 O(n)
  - 균형 트리 최악 시간복잡도 O(logn)
- 항상 정렬된 상태
- 데이터가 많아지면 depth가 늘어남
- FullScan시 모든 노드 탐색

### 2. Hash
- 인덱스의 크기가 작음
- 검색속도가 매우 빠르다.
- **충돌되는 경우가 많다.**

## Index의 종류

### 1. Clustered Index
- 한 테이블에 오직 하나만 존재 가능
- leaf에 실제 데이터가 저장된다.
- 테이블에 데이터가 삽입되는 순서에 상관없이 Index Key를 기준으로 **정렬**된다.
  - email을 ClusteredIndex로 만든다면? --> 성능저하 
  - ALTER를 통해 많은 데이터가 존재하는 곳에 ClusteredIndex를 설정한다면? --> boom
- default로 PrimaryKey에 지정된다. 
  - PrimaryKey에 강제적으로 NonClusteredIndex 지정도 가능하다.
  - 테이블에 이미 ClusteredIndex가 있으면 PK가 NonClusteredIndex가된다.

### 2. NonClustered Index
- Key값만 정렬되어 있고, Data는 정렬되어 있지 않다.
- 테이블 당 여러개가 존재한다.
  - 최대 249개 생성이 가능하다.
- ClusteredIndex보다 검색은 느리지만, 삽입,수정,삭제는 빠르다.
  - SEARCH : Index 검색 후 실제 데이터위치를 확인하여 접근하기 떄문이다.
  - CUD: 실제 Data를 정렬하지 않기 때문이다.
- NonClusterdKey는 정렬되지않는다. (포인터 형식)
- leaf에 실제 데이터의 주소가 저장된다. 

## 인덱스를 사용해야 할 지점
1. where절에 주로 사용돠는 Column
2. between A and B에 주로 사용되는 Column (ClusteredIndex가 유리)
3. order by에 주로 사용되는 Column
4. Join On 조건에 주로 사용되는 Column
5. ForeignKey (1 : N)에 주로 사용 되는 Column
6. **Cardinality**가 높은곳 (겹치는게 적어야한다. )
