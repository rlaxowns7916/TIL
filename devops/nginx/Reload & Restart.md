# reload & restart
- nginx를 재가동하는 방법이다.

## Realod
- 설정파일을 다시 로드한다.
- Server를 죽였다가 다시 로드하는 것이 아니다.
- 설정파일 리로드시, 서버를 종료하고 새롭게 띄우는 것이 아니기 때문에 장애를 방지 할 수 있다.

### 과정
1. Master Process가 Configuration의 문법 유효성을 검즘한다.
2. Master Process가 새로운 설정을 적용한다.
3. 이전 WorkerProcess들에게 하던 작업을 마치고 종료하라는 명령을 보낸다.
4. 새로운 WorkerProcess를 생성한다.

***

## Restart
- 서버를 중단시킨 후 재가동 한다.
- 새로운 설정파일에 문법적 오류가 있다면, 서버가 새롭게 동작하지 못하게된다.