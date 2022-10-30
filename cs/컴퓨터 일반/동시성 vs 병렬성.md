# 동시성 vs 병렬성

## 동시성 (Concurrency)
- 동시에 **여러작업이 수행되는 것 처럼** 보이는 것.
- Single Core - Multi Thread
  - 하나의 실행주체가 하나씩 많은 것을 빠르게 처리
  - 여러개
- 병렬성보다 유리한 경우가 있다.
  - I/O (Network, File Load)
    - 로드가 될 때 까지 blocking되면서 기다리기 때문이다.
    - 로드가 될 때 까지 기다리면서 다른일을 처리하면 좋다.

## 병렬성 (Parallelism)
- 실제로 동시에 여러 작업이 수행되는 것이다.
- Mutl Core - Multi Thread
  - 여러개의 실행 주체가 각각 작업을 실행