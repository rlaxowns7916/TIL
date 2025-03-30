# Route53
- 고가용성, 확장성을 갖춘 DNS
- Domain등록, Record 관리, HealthCheck, Routing기능 제공
- 고객이 직접 DNS Record를 업데이트 할 수 있다.
- 100% SLA를 보장하는 유일한 AWS 서비스이다.

## Records
- Domain에 대한 트래픽을 제어하는 방법
- 아래와 같은 정보를 갖는다.
  - Domain/SubDomain Name: ex) example.com
  - RecordType: ex) A, AAAA
  - value: Domain에 매핑된 IP 혹은 Domain
  - Routing Policy: 어떻게 Route53이 DNS-Query에 응답할지
  - TTL: DnsResolver 들이 얼마나 Record를 캐싱할지

### RecordType
- A: IPV4에 Mapping되는 HostName
- AAAA: IPV6에 Mapping되는 HostName
- CNAME: HostName에 매핑되는 또다른 HostName 
- NS: Hosted Zone에 대한 NameServer (트래픽 제어 룰을 담당한다)

## HostZone
- DNS레코드를 저장/관리 하는 저장소
- 어떤 방식으로 Routing 할지 결정되는 단위이다.
- Public HostZone: Public망 (인터넷)에서 접근 가능한 Domain
- Private HostZone: Private망 (내부)에서만 접근 가능한 Domain
  - Route53의 경우, VPC 내부에서 접근가능
- HostZone 하나당 월 0.50$의 요금이 부과된다.

### TTL
- 클라이언트가 DNS쿼리를 요청했을 때, DNS서버가 응답한 결과를 얼마나 캐싱할지 결정하는 시간
- TTL이 만료되면, 다시 DNS Query를 수행한다.
  - 너무 TTL기간을 적게 설정하면, DNS서버에 부하가 증가한다.
  - TTL기간을 너무 길게 설정하면, DNS Record의 변경이 반영되기까지 시간이 걸린다.
- alias 레코드는 제외된다.

### CNAME vs Alias
- CNAME: 다른 HostName을 가리키는 DNS 레코드
  - CNAME은 반드시 RootDomain이 아닌 SubDomain에만 사용해야 한다.
  - ex) example.com(x), www.example.com(o)
- Alias: Aws에 국한
  - DNS 기능의 확장이라고 볼 수 있다. 
  - **CNAME은 비용이 부과되지만, Alias는 비용이 부과되지 않는다. (Route53내부에서 처리되기 떄문)**
  - 특정 HostName이 AWS의 리소스에 매핑되는 레코드
    - 실제 IP를 몰라도 되고, AWS 리소스가 변경되더라도 자동으로 반영된다. 
    - 매핑 가능한 리소스
      - ELB,
      - CloudFront
      - S3 (웹사이트 한정)
      - Route53 (Same HostedZone)
      - ElasticBeanstalk
      - API Gateway
      - Global Accelerator
      - ...
    - 매핑 안되는 리소스
      - EC2 (DNS 이름)
- RootDomain, SubDomain 모두 동작한다.
- A 혹은 AAAA 타입만 지원한다.
- TTL 없이 사용 가능하다.
  - 내부적으로 TTL이 자동으로 관리된다.
  - AWS 리소스의 IP주소 변경을 자동으로 추적한다.