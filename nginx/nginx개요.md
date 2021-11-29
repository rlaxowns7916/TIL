# Nginx
- 동시접속에 특화된 WebServer
- Apache 보다 동작이 단순
- Apache보다 확장모듈이 부족
- OSI 7 Layer에서 ApplicationLayer 아래의 Level에서 동작
- 비동기 이벤트 구조로 동작하여 고정된 프로세스만 생성 
  (Apache는 Request당 스레드 혹은 프로세스 생성)
- **WebServer**의 역할과 **ReverseProxy(LoadBalancing)** 역할을 주로한다.

## Nginx의 구조
**하나의 Master Process와 다수의 WorkerProcess로  구성**

- Master Process는 설정 파일을 읽는 역할
- 유효성 검사 및 모든 요청에 대한 처리는 WorkerProcess가 담당
- WorkerProcess 사이에 요청을 효율적으로 분배하기 위해서 **OS의존적**인 메커니즘 사용
- WorkerProcess의 개수는 설정파일에 정의, 사용가능한 CPU 코어에 맞게 조정된다.


## 설치
```shell
sudo apt-get install nginx

# nginx에 관한 파일들이 있는 디렉토리
cd /etc/nginx
```
