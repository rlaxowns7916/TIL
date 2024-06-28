# cat API
- Compact and Aligned Text (CAT) API
- https://www.elastic.co/guide/en/elasticsearch/reference/current/cat.html
- UI가 아닌, REST API를 통해서 ES의 정보를 얻을 수 있는 API이다.

## [1] cat health
```shell
# v: verbose -> header정보를 포함한다.
GET {es-endpoint}/_cat/health?v

# 총 3개의노드, 3개의 노듬 모두 DataNode로 역할 중
epoch      timestamp cluster      status node.total node.data shards pri relo init unassign pending_tasks max_task_wait_time active_shards_percent
1718531596 09:53:16  log-es-1     green           3         3    939 672    0    0        0             0                  -                100.0%
```
- status
  - green: 모든 shard가 정상적으로 동작중 
  - yellow: 모든 primary shard는 동작중이지만, replica shard 중 일부가 동작하지 않음 (색인에는 문제가 없으나, 검색에 이상있음)
  - red: 일부 primary shard가 동작하지 않음 (색인, 검색 모두 이상이 있을 수 있음)
- node.total: 전체 node 갯수
- node.data: data node 갯수
- shards: 전체 shard 갯수
- pri: primary shard 갯수
- relo: reloaction 중인 shard 갯수 (dataNode를 Cluster에 새롭게 추가하거나..)
- init: initializing 중인 shard 갯수
- unassign: 할당되지 않은 shard 갯수
- pending_tasks: 대기 중인 task 갯수
- max_task_wait_time: 가장 오래된 task의 대기 시간
- active_shards_percent: 활성화된 shard의 비율


## [2] cat nodes
- 전반적인 node들의 상태를 알 수 있다.
- ```shell
  GET {es-endpoint}/_cat/nodes?v
  ```
  ip            heap.percent ram.percent cpu load_1m load_5m load_15m node.role   master  name
                 53                   95   6    0.52    0.60     0.59  cdfhilmrstw  -     node-1-master
                 42                   99   7    0.76    0.82     0.72  cdfhilmrstw  -     node-3-master
                 58                   97   5    0.74    0.66     0.63  cdfhilmrstw  *     node-2-master
  ```
- heap.percent: heap memory 사용량 (jvm)
- ram.percent: ram 사용량 (node)
- node.roles
  - c (Cold Node): 덜 빈번하게 접근되는 데이터를 저장. 
  - d (Data Node): 색인과 검색 요청을 처리하며, 데이터를 저장 
  - f (Frozen Node): 극히 드물게 접근되는 데이터를 저장
  - h (Hot Node): 가장 자주 접근되는 데이터를 저장
  - i (Ingest Node): 데이터가 인덱싱되기 전에 전처리 파이프라인을 통해 데이터를 변환하거나 기타작업을 수행
  - l (ML Node - Machine Learning Node): 머신러닝 작업을 수행 (이상 탐지, 데이터 분류, 예측 모델링)
  - m (Master-Eligible Node): 클러스터에서 Master 후보노드
  - r (Remote Cluster Client Node):다른 클러스터와의 통신을 통해 데이터를 검색
  - s (Searchable Snapshot Node): 검색 가능한 스냅샷을 처리(읽기 전용 스냅샷에서 검색 작업을 수행 가능)
  - t (Transform Node): 트랜스폼 작업을 처리 (집계, 변환하여 다른 인덱스에 저장가능)
  - w (Warm Node): 핫 노드보다 덜 빈번하게 접근되는 데이터를 처리