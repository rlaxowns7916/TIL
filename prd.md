# PRD: [FIX] Kafka 트랜잭션/EOS 문서의 권위 소스 정리 및 컨텍스트 갱신

## 목표
- PR #6(feat/kafka-tx-20260203) 문서의 참고문헌을 **공식/권위 소스 중심**으로 재정렬한다.
- `web/backend/kafka/CONTEXT.md`의 공식 출처와 성숙도를 현재 수준으로 맞춘다.

## 작업 범위
- `web/backend/kafka/Kafka-트랜잭션-심화.md`
  - 참고 문헌을 Apache Kafka 공식 문서 + KIP 중심으로 교체
- `web/backend/kafka/CONTEXT.md`
  - Authoritative Sources 구체화
  - Technical Maturity 최신화

## 비범위
- 신규 실습 코드/샘플 추가
- 다른 Kafka 문서(Producer/Consumer 등) 대규모 리라이트

## 권위 소스(교차 검증 기준)
- Apache Kafka Documentation
- Kafka Improvement Proposals (KIP)
- Apache Kafka GitHub

## 완료 조건(DoD)
- [ ] 문서 참고문헌이 최소 3개 권위 소스로 구성
- [ ] CONTEXT.md 업데이트
- [ ] 커밋/푸시 후 PR #6 업데이트 완료
