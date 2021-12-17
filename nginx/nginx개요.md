# Nginx
- 동시접속에 특화된 WebServer
- Apache 보다 동작이 단순 (Apache는 요청당 Process OR Thread 할당)
- Apache보다 확장모듈이 부족
- OSI 7 Layer에서 ApplicationLayer 아래의 Level에서 동작
- 비동기 이벤트 드리븐 구조로 동작하여 고정된 프로세스만 생성 하고 동시성 탁월
  (Apache는 Request당 스레드 혹은 프로세스 생성)
- **WebServer**의 역할과 **ReverseProxy(LoadBalancing)** 역할을 주로한다.

## Nginx의 구조
**하나의 Master Process와 다수의 WorkerProcess로  구성**

- Master Process는 설정 파일을 읽는 역할
- 유효성 검사 및 모든 요청에 대한 처리는 WorkerProcess가 담당
- WorkerProcess 사이에 요청을 효율적으로 분배하기 위해서 **OS의존적**인 메커니즘 사용
- WorkerProcess의 개수는 설정파일에 정의, 사용가능한 CPU 코어에 맞게 조정된다.

## Nginx의 역할
- 정적파일을 제공하는 웹서버로서의 역할
- 리버스 프록시로서의 역할

## 설치 
```shell
# /etc/apt/soruces.lists.d 경로에 nginx.list파일 생성
sudo touch /etc/apt/sources.list.d/nginx.list
deb http://nginx.org/packages/ubuntu/ bionic nginx
deb-src http://nginx.org/packages/ubuntu/ bionic nginx


# 인증 키 등록
wget http://nginx.org/keys/nginx_signing.key
sudo apt-key add nginx_signing.key

# 설치
sudo apt-get install nginx

#시작
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