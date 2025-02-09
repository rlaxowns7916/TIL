# Virtual Memory(가상메모리)
- 물리적인 Memory(RAM)보다 더 많은 Memory를 사용가능하게 해주는 기술
  - LogicalAddress 체계를 이용하여 수행된다. 
  - Process를 사용하는데 모든 영역을 Memory에 올리지 않아도 된다.
  - 필요한 영역만 Memory에 올리고, 나머지는 Disk에 위치시킨다.
- MMU(MemoryManagementUnit)가 PageTable(or SegmentationTable)을 이용하여, PhysicalAddress와 LogicalAddress 변환을 지원한다.
  - H/W 부품이다.
  - PageTable: Page - Frame 매핑 테이블
  - SegmentationTable: BaseAddress(Physical Address 시작점), Limit (길이)
# 주요 개념
- Demand Paging: 페이지가 실제로 필요할 때만 로드합니다.
- 페이지 교체 알고리즘
  - LRU(Least Recently Used): 가장 오래전에 사용된 페이지를 교체.
  - LFU(Least Frequently Used): 가장 참조횟수가 적은 페이지를 교체.
  - FIFO(First-In, First-Out): 먼저 들어온 페이지를 교체. 
  - OPT (Optimal): 앞으로 가장 오랫동안 사용하지 않을 페이지를 교채 
  - ...

# VirtualMemory의 장점
1. 프로세스가 물리적인 메모리제약에서 벗어날 수 있음
2. 각 프로세스가 적은 양의메모리를 소유하고 필요한 양만 로드 -> 동시에 많은 프로세스 실행 가능 (DemandPaging)
3. 프로세스를 메모리에 올리고 swap하는데 필요한 I/O 횟수가 줄어듬 
4. 각 Process는 독립적인 VirtualMemory공간을 가지며, 다른 Process의 Memory에 접근 불가능하다.
   - Process별 독립적인 PageTable(SegmentTable)을 가지고 있기 때문이다.
   - MMU의 Switching시 같은 VirtualAddress 여도 실제로는 다른 PhysicalAddress에 매핑되기 떄문이다.

# VirtualMemory 기법

|  | **Paging(페이징)**                              | **Segmentation(세그먼테이션)**                    |
|---|----------------------------------------------|---------------------------------------------|
| **기본 단위** | **고정 크기(Page)** (ex: 4KB)                    | **가변 크기(Segment)**                          |
| **주소 변환** | **페이지 테이블(Page Table)** 사용                   | **세그먼트 테이블(Segment Table)** 사용              |
| **단편화 문제** | **내부 단편화(Internal Fragmentation)** 발생 가능     | **외부 단편화(External Fragmentation)** 발생 가능    |
| **주소 구성** | **논리적 주소 = 페이지 번호 + 페이지 오프셋**                | **논리적 주소 = 세그먼트 번호 + 오프셋**                  |
| **사용 방식** | **현대 운영체제(Windows, Linux, macOS 등)에서 기본 사용** | **과거(80286 이전)에서 많이 사용되었으나 현재는 거의 사용되지 않음** |

- Page크기가 적다면, 내부 단편화는 적게일어나겠지만, MMU가 관리하는 테이블이 커지고 매핑에 시간이 들어 성능이 저하될 수 있다.
- Page크기가 크다면, 내부 단편화가 많이 일어나며, MMU가 관리하는 테이블의 크기가 줄어들어 I/O 수행 시간이 줄어들 수 있다. 