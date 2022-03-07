# gRPC
- Google에서 개발
- Binary Protocol 사용
- 모든 환경에서 실행 할 수 있는 오픈소스 고성능 RPC 프레임워크
- TCP/IP 프로토콜과 HTTP 2.0을 사용한다.
- IDL(Interface Definition Language)로 Protocol Buffer를 사용한다.

## RPC(원격 프로시저 호출)란?
- 원격에 존재하는 Procedure(함수)를 로컬에 존재하는 것처럼 호출 하는 것.
- IPC(Inter-Process-Communication)의 한 형태
- Cleint-Server 모델을 사용한다.
- 네트워크의 세부정보를 이해하지 않고도, 다른 네트워크의 프로그램에게 서비스를 요청하는 것


## IDL(Interface Definition Language)
- 정보를 저장하는 규칙이다.
- XML, JSON, ProtocolBuffer등이 있다.

## Protocol Buffer
- 간단하다.
- 파일크기가 3배에서 10배가량 작다.
- 속도가 20배에서 100배 가량 빠르다.
- XML보다 가독성이 좋고 명시적이다.
- 내장 기능이 풍부하다(인증,암호화,압축 ... )

## 권장 시나리오
1. MSA
    - 대기시간이 짧고, 처리량이 높기 때문에 효율적
    - 브라우저가 필요없는 백엔드서버간 통신에 용이
2. 실시간 양방향 통신
    - 양방향 실시간 통신을 제공
    - Polling 방식이 아닌, 실시간 Push를 사용
3. Polygot 환경
    - 모든 개발언어를 지원
4. IPC
    - 동일한 머신 안에있는 앱간의 통신에 사용이 가능하다.

## 단점
1. 제한된 브라우저 자원
    - 브라우저에서 직접적인 호출이 불가능하다.
2. 사람이 읽을 수 없다.
    - Protobuf로 인코딩되어 전송되어 사람이 읽을 수 없다.