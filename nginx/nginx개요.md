# Nginx
- 동시접속에 특화된 WebServer
- Apache 보다 동작이 단순
- **비동기 이벤트 드리븐 구조**로 동작하여 고정된 프로세스만 생성 하고 동시성이 탁월하다.
- **WebServer**의 역할과 **ReverseProxy(LoadBalancing)** 역할을 주로한다.
- C10K를 해결했다.

## C10kCurrent 10 thousand clients)란?
- 1만개의 커넥션를 동시에 처리하는 문제이다.
- 하드웨어 성능에는 문제가없어도, 클라이언트수가 많아지면 서버가 정상작동하지 않는다.

1. Client 접속 당 프로세스를 생성하면, OS 파일 디스크립터나, 프로세스 수가 최대치가 된다.
2. Process 당 소비하는 메모리의 크기는 작지만, 이것이 모여서 거대해진다.
3. ContextSwitching에 따른 CPU 사용률이 문제가 된다.
4. Process를 관리하는 OS 커널 내의 관리용 데이터 크기의 문제가 생긴다.

### Apache
- Thread/Process 구조
- PreFork 모델
  - Request에 자식 프로세스를 생성 및 처리
- Worker 모델
  - Reqeust에 자식프로세스의 여러 쓰레드를 사용한다.
- C10K문제에서 자유롭지 않다.
- 많은 Connection에서 요청이들어오면? --> ContextSwitching비용의 상승
  - Connection은 Keep-Alive인데 Request는 들어오지 않는다면?? --> 일을하지않고 방치된다.
- Thread Pool로 문제를 보완하려 하지만, 그 이상 요청이들어오면 추가로 Thread를 생성한다.

## Nginx의 구조
- EventDrivenModel 
  - 여러 Connection을 EventHandler에서 처리해서, 먼저 처리되는 것 부터 로직이 실행되도록한다.
  - 다수의 Connection을 효과적으로 처리한다.
- 1개의 Thread가 여러개의 Request를 담당한다.
- Event의 효율적인 분배를 위해서 **OS의존적**인 메커니즘 사용
  - os가 이벤트를 큐에 담아놓고 워커프로세스가 빼가면서 처리
  - 시간이 오래걸릴 것 같은 작업은 ThradPool에 위임한다.

### Event
- Connection 생성
- Connection 제거
- Request 처리

### Master Process
- 한개만 생성된다.
- 설정파일을 읽는 역할을 한다.
- WorkerProcess를 생성하는 역할을 한다.
- 설정 동적 리로드 가능
  - 동적 리로드 시, 새로운 설정에 맞는 워커 프로세스를 생성 한 후 기존 워커프로세스들의 작업이 끝나면 기존 워커프로세스들은 종료

### Worker Process
- N개의 Connection을 담당한다.
- 유효성 검사 및, 요청에 대한 처리를 담당한다.
- 보통 CPU Core 개수만큼


## Nginx의 역할
- 정적파일을 제공하는 웹서버로서의 역할
- 리버스 프록시로서의 역할
- SSL 터미네이션: Client와는 HTTPS, 내부 서버와는 HTTP
  - 뒤의 was가 복호화과정을 담당하지 않음으로하여, 부하줄여줌  

## 설치
```shell
sudo apt-get install nginx
```
## 시작
```
sudo /etc/init.d/nginx start
```

## 디렉토리 구조
1. **/etc/nginx** <br>
엔진엑스 서버가 사용하는 기본설정이 있는 루트 디렉토리

2. **/etc/nginx/nginx.conf**<br>
엔진엑스 기본설정파일, 모든 설정의 진입점<br>
글로벌 설정(워커프로세스 개수, 튜닝 등) 뿐만 아니라 다른 세부설정 파일의 참조도 지정<br>
모든 설정을 포함하는 최상위 http블록도 갖고있다.

3. **/etc/nginx/conf.d/**<br>
기본 HTTP 서버 설정파일 포함 <br>
nginx.conf에 불러들일 수 있는 설정파일들을 가질 수 있는 디렉토리 

4. **/var/log/nginx**<br>
accessLog와 errorLog를 갖고있다.

5. **/etc/nginx/conf.d/defalut.conf**<br>
정적 콘텐츠 제공

## 명령어
```shell
# 도움말
nginx -h

# 버전
nginx -v

#버전+빌드 정보
nginx -V

#설정 시험 및 결과표시
nginx -T

#마스터프로세스에 시그널 전달
#stop(즉시 동작멈춤),quit(현재 실행중인 요청 모두 수행 후 종료) ,reload(설정파일 다시 읽기),reopen(로그 파일 다시 열기)
nginx -s [signal]

```
