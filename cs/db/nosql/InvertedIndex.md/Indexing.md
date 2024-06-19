# 색인 (indexing)
- 데이터를 검색 할 수 있는 형식으로 바꾸기 위해서 문서를 분석하고 저장하는 과정이다.
- 원본 문서를 Token으로 쪼개어, 검색 할 수 있는 형태로 변환한다.
  - 문서 저장
  - Index 생성
  - Mapping 확인 및 생성
  - InvertedIndex 생성
    - Inverted Index를 생성하는 것이 가장 무거운 작업이다.
- 색인의 성능을 높이기 위해서는 Cluster의 이점을 충분히 누리고있는지 확인해야 한다.
- 적절한 수의 Shard를 설정해야 한다.
  - 적절한 PrimaryShard를 사용하여, 색인의 성능을 높여야한다.
  - Shard의 개수에 따라서, 고르게 분배되지않고 Shard마다 용량의 불균형이 발생 할 수 있다.
- 성능에 문제가있다면, Cluster 로서의 이점을 잘 살리고있는지 점검해봐야한다.

## Indexing 예시
```text
하루에 100GB의 로그를 30일간 저장하는 클러스터
- DataNode: 10개
- PrimaryShard: 1개
- ReplicaShard: 1개
- Shard의 최대크기 10GB 

필요한 저장공간
100GB * 2(Primary, Replica) * 30일 = 6TB

Data Node 당 가져야 할 Disk 용량
600GB
```

## 색인 과정

<img width="1466" alt="색인과정" src="https://user-images.githubusercontent.com/57896918/213202126-247135ff-4f68-4ee4-b112-c750623f2ddb.png">

# 역 인덱스 (Inverted Index)
- 키워드에 해당하는 문서를 역으로 매핑하는 과정
  - 검색은 문서에 해당하는 키워드를 찾는 행위이기 때문에, 키워드에 해당하는 문서들을 가지고 있는 것이 훨씬 빠르다.
- **ElasticSearch는 Row가 늘어나는 것이 아니라, 해당하는 문서가 배열에 추가 되는 것이기 떄문에, 속도가 크게 저하되지 않는다.**
  <img width="820" alt="스크린샷 2023-01-18 오후 11 07 59" src="https://user-images.githubusercontent.com/57896918/213202294-32530a91-d289-4020-8fa1-de150ca50523.png">
