# Region & AZ
- 전세계에 Global하게 퍼져있는 AWS의 서비스 가용영역
- 각 Region에 AZ가 존재한다. (1:N)
  - ap-northeast-2(Region)
  - ap-northeast-2a, ap-northeast-2b, ap-northeast-2c (AZ)

## Region 선택 조건
1. Compliance
   - 해당 지역 법률에 의거
2. Proximity
   - 사용자들의 Latency를 줄이기 위함
3. Pricing
   - Region 마다 요금이 상이함

## AZ(AvailabilityZone)
- Region마다 AZ의 갯수는 다르다.
  - (min:2, max: 6, usually: 3)
- 각각의 AZ는 한개 이상의 DataCenter를 가지고 있다. (HA 목적)
- 각각의 AZ는 장애에 대비해 물리적으로 단절되어 있다.
- AZ는 서로 고대역폭 초저지연 Network를 통해서 연결되어 있다.
  - AZ내부에서의 통신은 (PrivateIP 사용시) 무료이다.
  - AZ끼리의 통신은 유료이다.
