# EFS(Elastic File System)
- AWS제공하는 완전 관리형 NAS
  - Linux기반의 NFS 프로토콜을 사용한다. (기존의 온프레미스 방식과 유사)
  - **Linux기반 AMI에만 호환된다.**
- 여러 EC2 Instance에서, 여러 AZ에서 동시에 공유 가능하다.
- AutoScaling이 지원된다.
- 비싸다. (사용량에 따라 지불하며, EBS gp2볼륨의 3배)
- 동시에 1000개의 NFS Client와, 10GB+ 의 처리량을 보인다.
- 사용한 스토리지에 대한 비용만 지불한다.

## Performance Mode (EFS 생성시에 설정가능)
1. General Purpose (Default): 지연시간에 민감한 경우에 사용 (Web Server, CMS, ...)
2. Max I/O - 지연시간이 길지만, 처리량과 병렬성이 높다. (BigData Analyze, Machine Learning, ...)

## Throughput Mode
1.	Elastic (기본값)
   - 워크로드 변화에 따라 자동으로 처리량이 조정됨. (예측할 수 없는 경우에 좋다.)
   - 데이터 저장 용량과 관계없이 필요한 만큼 처리량을 제공.
   - 가장 유연한 옵션으로, 기본 설정값임.
2. Bursting
   - 저장된 데이터 용량에 비례하여 처리량이 증가.
   - 기본적 으로 1TB당 50MiB/s의 처리량을 제공하며, Burst Credit을 사용하면 최대 100MiB/s 이상까지 증가 가능.
   - Elastic 모드가 도입되기 전까지 기본값이었으며, 현재는 선택적으로 사용 가능.
3. Provisioned
   - 저장 용량과 관계없이 설정한 처리량을 보장.
   - 추가 비용이 발생하지만, 예측 가능한 고정된 성능을 제공

## Storage Class (LifeCycle Management)
1. Standard(Default): 자주 접근하는 데이터에 유용하며, MilliSec 미만의 지연을 제공한다.
2. InfrequentAccess(IA): 자주 접근하지않는 데이터(분기에 몇번) 를 위한 저비용 옵션
3. Archive: 자주 접근하지 않는데이터(1년에 몇번)을 위한 장기저장데이터에 특화된 EFS이며, 성능은 IA와 유사하다.