# Docker Context

## 🎯 Mission
- 컨테이너 기술의 핵심 메커니즘을 파악하고, 개발-테스트-운영 전반에 걸친 인프라 표준화 역량을 확보한다.
- 단순히 명령어를 나열하는 것을 넘어, 성능 최적화(BuildKit, Layer Caching)와 보안(Secrets, Non-root)이 고려된 전문가 수준의 지식을 축적한다.

## 📚 Authoritative Sources (공식 출처)
- [Docker Official Documentation](https://docs.docker.com/): 최우선 참조 가이드.
- [Moby Project GitHub](https://github.com/moby/moby): Docker Engine의 핵심 소스 및 상세 사양.
- [BuildKit GitHub](https://github.com/moby/buildkit): 빌드 최적화 및 확장 기능 명세.
- [Docker Cache Backends](https://docs.docker.com/build/cache/backends/): BuildKit 캐시 저장소 정밀 명세 (2026-02-02 리서치).

## 🛠️ Subject-Specific Conventions (폴더 전용 규칙)
- **Builder Standard**: 모든 빌드 관련 문서는 기본적으로 `BuildKit`을 기준으로 기술하며, 레거시 빌더와의 차이점을 명시한다.
- **Optimization Priority**: 레이어 캐싱 효율을 극대화하기 위한 Dockerfile 작성 패턴을 항상 예제에 포함한다.
- **Multi-stage First**: 배포용 이미지 작성 시에는 반드시 멀티 스테이지 빌드 패턴을 기본으로 제안한다.
- **Cache Strategy**: 외부 캐시 백엔드(registry, gha 등) 사용 시 `mode=max`를 우선적으로 고려한다.

## 📈 Technical Maturity (학습 성숙도)
- [x] 기본 개념 (이미지, 컨테이너, 볼륨, 네트워크)
- [x] 빌드 시스템 (BuildKit, Multi-stage)
- [x] 빌드 캐시 최적화 (Cache Backends 분석 완료)
- [ ] 오케스트레이션 기초 (Swarm, Compose 심화)
- [ ] 보안 및 거버넌스 (Scan, Content Trust)
