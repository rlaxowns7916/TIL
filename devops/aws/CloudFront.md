## CloudFront
- **CDN**: Edge Location에 컨텐츠를 캐시하여 전세계 사용자에게 낮은 지연 시간으로 제공
- **TTL**: 기본 24시간
- **DDoS 방어**: AWS Shield, AWS WAF 연동 가능
- **Global 서비스**: 리전 선택 불필요
- **지리적 제한**: Geo Restriction 설정 가능

### Distribution 구성 요소

| 구성 요소            | 역할 및 설명                                                                                                      |
|---------------------|------------------------------------------------------------------------------------------------------------------|
| **Edge Location**   | 전 세계에 분산된 캐시 서버. 사용자 근접 Edge에서 컨텐츠 제공, 지연 시간 최소화                                        |
| **Origin**          | CloudFront가 컨텐츠를 가져올 근원 서버(S3 버킷, ALB/EC2, HTTP 서버 등)                                              |
| **Origin Shield**   | Origin 앞 글로벌 캐시 계층. 여러 Edge Location 요청을 통합하여 Origin 부하 완화                                      |
| **Cache Behavior**  | URL 패턴별 요청 처리 방식을 정의(Path Pattern, Viewer Protocol Policy, Allowed Methods, Cache Policy 등)           |

---

## Caching 전략과 정책

### Cache Policy & Origin Request Policy
- **Cache Policy**: Cache Key(Headers, Query String, Cookies 포함 여부) 구성, TTL(default/min/max) 설정
- **Origin Request Policy**: Origin에 전달할 Header/QueryString/Cookie 제어
- **Managed vs Custom**: AWS 제공 기본 정책 또는 애플리케이션 요구사항에 맞춘 커스텀 정책 선택 가능

### TTL 설정
- `Default TTL`: 24시간
- `Min TTL`: 0초 (동적 컨텐츠 캐싱 방지)
- `Max TTL`: 1년

### Invalidation & Versioning
- **Cache Invalidation**: 특정 경로(또는 와일드카드) 지정하여 즉시 만료
- **버전 관리**: URL 쿼리 파라미터(`?v=123`) 또는 파일명(`style.v123.css`) 방식으로 캐시 갱신

---

## 보안(Security)

### Origin Access Identity (OAI) vs Origin Access Control (OAC)

| 항목         | OAI (구)                      | OAC (신)                                                      |
|-------------|------------------------------|--------------------------------------------------------------|
| 지원 Origin | S3 버킷 전용                  | S3, ALB, EC2, 외부 HTTP 서버 등 모든 Origin 지원            |
| 인증 방식   | OAI 아이덴티티 기반          | SigV4 서명 헤더 기반 + CloudFront OAC ID 기반 정책 설정     |
| 장점        | 간단하지만 S3 전용           | 모든 Origin 지원, 강력한 인증(SigV4), 권한 세분화 가능     |

1. **OAC 생성**: CloudFront 콘솔 또는 API에서 OAC 생성
2. **Origin 설정**: Distribution의 Origin 설정에서 OAC 선택
3. **Origin 정책 수정**: S3 버킷 정책 또는 ALB Security Group/IAM Role에 OAC Principal 허용

### HTTPS·TLS 관리
- **Viewer Protocol Policy**: HTTP→HTTPS 리디렉션 또는 HTTPS 전용
- **TLS Termination**: CloudFront가 TLS 핸드셰이크 처리 후 Origin과 통신
- **Custom SSL Certificate**: ACM 인증서 연결

### AWS WAF·Shield
- **WAF**: 웹 공격 방어 규칙 적용
- **Shield**: Standard(기본 포함) / Advanced(추가 비용)

---

## 고급 기능

- **Lambda@Edge**: Viewer 요청/응답 단계에서 Node.js/Python 코드 실행
- **CloudFront Functions**: 초저지연 JavaScript 실행 (URL 리라이팅, 헤더 조작 등)
- **Field‑Level Encryption**: 민감 데이터(PII) 암호화 전송

---

## 모니터링·로깅

- **Access Logs**: S3에 요청 로그 저장 (응답 코드, 지연 시간 등)
- **CloudWatch Metrics**: Requests, BytesDownloaded/Uploaded, 4xx/5xx Error Rate, CacheHitRate
- **Real‑Time Logs**: Kinesis Data Streams로 실시간 분석

---

## 비용 최적화

### Price Class
- **Price Class 100**: 미국·유럽·아시아 일부
- **Price Class 200**: Price Class 100 + 남미·중동 일부
- **Price Class All**: 전 세계 모든 Edge Location

- **데이터 전송 비용**: CloudFront → 사용자 요금이 EC2/ALB 직접 송신보다 저렴

---

## Signed URL vs Signed Cookies

| 항목                   | Signed URL                             | Signed Cookies                                         |
|------------------------|-----------------------------------------|--------------------------------------------------------|
| 사용 시나리오           | 개별 객체 접근 제어                     | 여러 객체(폴더) 접근 제어                              |
| 구현 복잡도             | 간단 (URL에 만료 시간·서명 포함)        | 복잡 (쿠키 4개 세트: Policy, Signature, Key-Pair 포함) |
| 캐시 충돌 가능성        | 낮음 (URL별 고유 서명)                  | 높음 (쿠키 단위로 캐시 공유)                           |
| 정책 적용 범위          | URL 단위                                | 쿠키 정책 한 번 발급으로 여러 요청에 적용              |

---

## 요약

CloudFront를 설계할 때는 **캐시 정책(Cache/Origin Request Policy), 보안(OAC, WAF, TLS), 고급 기능(Lambda@Edge, Functions), 모니터링, 비용 최적화**까지 전반적으로 고려해야 합니다.  
**OAC**는 모든 Origin을 안전하게 보호하는 최신 권장 방식으로, 기존 OAI보다 우선 적용하는 것을 권장드립니다.

