# Disk Scheduling
- OS의 I/O를 처리하는 순서를 결정하는 알고리즘
- Disk의 Head 이동시간 (Seek Time)을 최소화 하여 성능을 최적화 하는 것이 핵심이다.

## Disk 구조
- 회전하는 플래터(Platter)와 이동하는 헤드(Head)로 구성됨
- 특정 Data에 접근하려면 Head가 해당 트랙으로 이동(SeeK) 해야 함
  - Disk I/O 요청이 많아지면, Head의 이동이 많아져 성능이 저하된다.


## SeekTime(=Track을 찾는데 걸리는 시간) 최적화 기법 

### [1] FCFS (FirstComeFirstService)
- 먼저 도착한 요청 우 선 처리
- 가장 간단하고 공평하다.
- Head의 이동거리에 비해서 처리율이 낮다.

### [2] SSTF (Shortest SeekTime First)
- Seek 거리가 작은 요청부터 처리 (현재 Head 위치에서 최소 SeekTime을 요하는 요청 먼저 처리)
- Starvation 문제 발생 
  - Track 외곽 (안쪽, 혹은 바깥쪽)이 중심부보다 Service를 받을 확률이 줄어들음
- FCFS보다 처리량이 많고, 응답시간이 짧지만 편차가 크다.

### [3] SCAN (엘리베이터 알고리즘)
- Head가 한방향으로 이동하면서 요청을 처리하고, 끝까지 도달하면 반대방향으로 가면서 처리하는 방식
- head의 방향이 일정하게 유지되서 서능이 향상된다.
- 반대방향에 있는 Track의 응답시간은 길어질 수 있다.


### [4] C-SCAN (Circular-SCAN)
- Scan의 성능 향상 버전이다.
- 한쪽 끝가지 이동 후 처음으로 이동
  - SCAN의 경우에는 처음이 아닌, 방향을 바꿔서 다시 진행이다.
- SCAN보다 응답시간이 일정하게 유지된다.
- 처음으로 돌아가는 것 때문에 추가적인 이동거리가 발생할 수 있다.

### [5] Look & C-Look
- SCAN, C-SCAN의 성능향상 버전
  - SCAN과 C-SCAN은 요청이 없더라도 Track 끝가지 이동한다. (불필요한 이동)
- Look & C-Look은 요청이 있는 지점 까지만 이동한다.
  - **현재 방향에 더이상 유효한 요청의 Track이 없다면 방향을 바꾼다.**