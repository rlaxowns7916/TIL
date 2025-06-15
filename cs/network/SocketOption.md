# Socket Option

## [1] SO_REUSEADDR
- 이미 Bind된 포트를 재사용할 수 있게 해주는 옵션

### 사용 이유
1. TIME_WAIT 싱태 우회
   - 서버가 재시작될 때 이전 연결이 TIME_WAIT 상태에 있으면 같은 포트로 즉시 바인딩할 수 없음
     - 보통 1~4분 정도 지속됨
   - SO_REUSEADDR를 설정하면 TIME_WAIT 상태의 소켓이 있어도 즉시 재바인딩 가능
2. 여러 소켓이 같은 포트 바인딩 (조건부)
   - 같은 주소와 포트에 여러 소켓이 바인딩되는 경우
   - 주로 멀티캐스트 수신이나 특수한 경우에 사용

### 주의사항 (INADDR_ANY)
```kotlin
val serverSocket = ServerSocket()
serverSocket.reuseAddress = true
serverSocket.bind(InetSocketAddress("0.0.0.0", 8080)) // INADDR_ANY
```
- 포트 하이재킹이 가능하다.
  - INADDR_ANY를 사용하면 모든 인터페이스에서 연결을 수신할 수 있음
  - 악의적인 프로세스가 특정 IP주소로 같은 포트에 바인딩이 가능해진다.
    - NIC가 여러개인 경우 가정
    - 특정 IP 바인딩이 INADDR_ANY보다 우선순위가 높다.
- IP 주소를 명시적으로 지정하는 것이 좋다.