# 멱등성(Idempotency) 설계: Web API + 메시징(Outbox/Queue/Kafka)에서 중복 처리 다루기

## 목표
- 재시도(retry), 타임아웃, 네트워크 오류가 **중복 요청/중복 메시지**를 만드는 이유를 정리한다.
- “정확히 한 번(exactly-once)”에 집착하기보다, **멱등한 처리(idempotent processing)** 로 시스템 신뢰성을 올리는 실무 패턴을 정리한다.

---

## 1) 멱등성의 정의 (HTTP 관점)
- 어떤 연산을 1번 수행하든 N번 수행하든 **최종 상태가 동일**하면 멱등이다.
- HTTP에도 “멱등 메서드”(GET/PUT/DELETE 등) 개념이 있지만,
  - 이는 **프로토콜 관점의 기대치**에 가깝고,
  - 애플리케이션 의미론(외부 결제/이메일/메시지 발행 같은 side-effect)까지 자동으로 보장해주진 않는다.

---

## 2) 중복이 발생하는 전형적인 이유

### (1) 클라이언트 타임아웃 + 재시도
- 서버는 처리/저장에 성공했지만, 응답이 네트워크에서 유실되면 클라이언트는 “실패”로 보고 재요청한다.

```
Client                     Server
  |---- POST /pay --------->|
  |        (timeout)        |
  |---- POST /pay --------->|  (2번째 요청)

서버가 1번째 요청을 이미 처리했다면, 2번째는 중복 결제/중복 생성 위험
```

### (2) 메시징의 at-least-once 전달
- 브로커/소비자 장애, 오프셋 커밋 타이밍 등으로 동일 메시지가 재전달/재처리될 수 있다.
- 결론: “중복은 정상 상태”로 가정해야 한다.

---

## 3) Web API 멱등성 패턴: Idempotency Key

### 핵심 아이디어
- 클라이언트가 요청마다 `Idempotency-Key`(또는 유사한 키)를 제공한다.
- 서버는 키를 기준으로 **처리 결과를 캐시/저장**하고, 같은 키로 재요청이 오면
  - **처리 재수행 없이** 이전 결과를 반환한다.

### ASCII 시퀀스

```
Client                                  Server (Idempotency Store)
  | POST /orders (Key=K1) ----------------->|  check K1
  |                                         |  not found -> process -> store result
  |<--------------------- 201 + orderId ----|

(응답 유실/클라이언트 재시도)
Client
  | POST /orders (Key=K1) ----------------->|  check K1
  |                                         |  found -> return stored response
  |<--------------------- 201 + same orderId|
```

### 구현 체크포인트
- 저장 스키마(예시)
  - `key`, `request_hash`, `status`, `response_body`, `created_at`, `ttl`
- **request_hash 검증**
  - 같은 키인데 다른 payload면 충돌로 간주(400/409)
- **TTL 설계**
  - 무한 저장은 비용/개인정보 리스크 → 정책 기반 TTL 필요
- 동시성
  - “키 선점”을 원자적으로 해야 함
  - DB unique index(key) + insert-once 패턴이 현실적

### 실무 팁
- 키 스코프(scope)
  - “고객 + 엔드포인트 + 키” 수준으로 스코프를 제한하는 것이 안전하다.
  - (예) Key 재사용을 허용하면, 의도치 않은 결과 재사용/재전송(replay) 위험이 커진다.
- 응답 재전송(Replay) 방지
  - 민감한 응답(PII 포함)을 저장한다면 **암호화/마스킹/짧은 TTL** 등 정책 필요.
- 실패 응답 캐시 정책
  - 모든 실패를 캐시하면 영구 실패처럼 보일 수 있다.
  - 일반적으로는 **성공(또는 비즈니스적으로 확정된 실패)** 만 저장하고,
    인프라 오류(5xx/timeout)는 저장하지 않는 정책을 고려한다.

---

## 4) Consumer(소비자) 멱등성 패턴: Dedup + Upsert

### (1) Message ID 기반 Dedup Store
- 메시지마다 dedup key를 정의한다.
  - (권장) business `event_id` (전 서비스/파이프라인에 걸쳐 유지 가능)
  - (대안) `(topic, partition, offset)`
- dedup 테이블에 insert 성공이면 처리, 중복이면 skip

> `(topic, partition, offset)`은 “현재 토픽 로그에서의 위치”이므로 재발행/재적재 시 동일 이벤트라도 값이 바뀔 수 있다.
> 장기적으로는 event_id를 이벤트 페이로드에 포함하는 것이 운영/감사에 유리하다.

### (2) 도메인 저장은 Upsert/조건부 업데이트
- 예: 주문 상태 전이(state transition)
  - 이미 PAID면 같은 이벤트가 와도 “상태 유지”
  - 이벤트 버전(version) 비교로 **out-of-order**에도 안전

---

## 5) Outbox 패턴과 멱등성의 결합
Outbox는 DB 트랜잭션으로 “상태 저장 + 이벤트 기록”을 묶어 발행의 신뢰성을 올린다.
하지만 Outbox/CDC/브로커 계층에서도 중복은 발생할 수 있으므로,
- 이벤트에는 **고유 `event_id`** 를 포함
- 다운스트림은 **`event_id` 기준 멱등 처리**를 수행

추가로, 프로듀서 측면에서도(가능한 경우)
- Kafka Producer Idempotence / Transaction API(EOS) 등을 활용해
  “재시도로 인한 중복 produce”를 줄이는 것이 전체 비용을 낮춘다.

---

## 6) 흔한 실패 사례
- (A) “HTTP는 PUT이 멱등이니까 안전하다”
  - 실제로는 DB insert/외부 결제/이메일 발송 등 side-effect가 있어 멱등이 깨질 수 있다.
- (B) dedup key를 “요청 전체 해시”로만 잡음
  - 필드 순서/정규화 차이로 동일 요청인데 해시가 달라지는 문제
- (C) TTL을 너무 짧게 둠
  - 재시도가 늦게 들어오면 중복 발생
- (D) 멱등 저장소(키/응답)가 단일 장애점(SPOF)
  - DB/캐시 장애 시 “중복 폭탄”이 날 수 있음 → 가용성/성능을 명시적으로 설계해야 함

---

## 7) 실무 결론
- 분산 환경에서 “중복 없음”을 보장하는 것은 비용이 크고(또는 불가능에 가깝고),
  **중복을 받아들이되 멱등한 처리**로 비즈니스 안전성을 확보하는 접근이 표준이다.
- Web API: **Idempotency-Key + 결과 저장(정책/TTL/보안 포함)**
- Messaging: **event_id 기반 Dedup + upsert/버전 기반 상태 전이**
- Outbox/CDC: **event_id 설계 + 소비자 멱등성**이 핵심

---

## 참고 문헌 (공식/표준)
- RFC 9110 (HTTP Semantics) — Idempotent Methods
  - https://www.rfc-editor.org/rfc/rfc9110
- Stripe Docs — Idempotency
  - https://stripe.com/docs/idempotency
- AWS Lambda Powertools — Idempotency (설계/구현 가이드)
  - https://docs.powertools.aws.dev/lambda/typescript/latest/utilities/idempotency/
- Google Cloud — Retry/Idempotency 관련 가이드(제품별)
  - https://cloud.google.com/apis/design/errors
