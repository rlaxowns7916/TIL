# HTTP
**서버와 클라이언트의 통신 규격**<br>
**헤더와 바디로 구성**

## HTTP 특징
1. Stateless
2. Connectionless (1 Connection == 1 File)
3. 거의 모든형태의 데이터를 주고 받는 것이 가능 
4. Request <  -- > Response 구조
5. 캐시 가능 


## Http Method
1. GET : 리소스 조회<br>
    - 전달하고 싶은데이터는 QueryString을 통해서 전달한다.
    - Body의 경우 보통 지원하지 않는다. 
  
2. POST: 요청데이터 처리
    - Body를 통해서 서버로 데이터 처리 요청
    - 주로 신규 데이터 등록, 프로세스 처리 등에 사용
3. PUT: 리소스 대체, 해당 리소스가 없다면 수정 
    - Post와의 차이점은 리소스의 위치를 알고 있다는 것
    - 리소스를 덮어씀(전체)
    - 멱등성 보장
4. PATCH: 리소스 부분 대체
    - 리소스 부분 변경 
    - 멱등성 보장(x)
5. DELETE: 리소스 삭제

- URL은 빈칸이 허용되지않아, Escape처리가 필요함(예외처리)
  - ' ' : '+' or %20
  - '+' : %2B(ASCII '+')
  - '%' : %25
    
- encode / decode 필요

### 멱등성(Idempotent)
**몇번을 호출하더라도 같은 결과가 나와야한다.**<br>
HTTP의 메소드는 멱등성을 보장한다.(**POST와 PATCH제외**)

1. POST: 두 번 호출 시, 같은 결제가 중복해서 발생한다. (그 때마다 다른 Response를 주게 될 것)
2. PATCH: 구현에대한 표준 제약이없다.<br>그렇기 때문에 구현방식에 따라서 보장 할수도 보장하지 못할 수도 있다.


## HTTP 메세지
[Header]
- GET / HTTP1.1 (HTTP메소드, HTTP 버전)<br>
(이름이 생략되면 보통 index.html(apache 계열) || default.html(iis 계열)))


- HTTP/1.1 200 OK (상태코드)


- content-length (파일 길이) // 네트워크에선 확장자 개념이 없다.<br>
  텍스트 파일의 경우 생략 가능(EOF) , 바이너리 파일은 필수


- content-type (파일 내용)

(헤더와 바디 구분은, 빈줄 2개 \r\n\r\n)

[Body]

## HTTP 에서 파일을 다운로드 하는법
**기존에는 하나의 Connection에서 하나의 File을 다운로드(GET)**

### 개선 
- 세션 : 하나의 세션을 열어 여러 파일 다운로드

- TCP keep-alive 
   - 일정시간동안 연결을 끊지 않음
   - 새로운 연결을 만들지않고, 기존 연결을 재활용
   - 한페이지를 다운로드 받는데 하나의 연결을 사용
   
- HTTP/1.1
 - 여러개의 파일을 동시에 요청 (Multiple GET)


## 버전
### HTTP 1.0
- 헤더가 생김
- 여러가지 파일 타입을 전송가능 (헤더의 Content-Type)

### HTTP 1.1
- Persistent Connection (지정한 시간동안 Connection을 닫지 않음)
- Pipelining (한번에 여러개의 요청을 같이보내고 순서에맞춰 응답여러개를 받음)
  - HeadOfLine: 첫번째 순서의 요청이 오래걸리면 그 뒤의 것들이 실행되지못함
  - Header구조의 중복

### HTTP 2
**표준의 대체가 아닌 확장**
- Binary Framing Layer
  - Frame단위로 분할, 및 Binary Format
  - 파싱, 전송속도의 상승
  - 오류 발생 가능성 저하
  - 프레임으로 쪼개져서 가기 떄문에 HOL문제의 해결
- Stream Prioritization
  - 리소스간 우선순위를 설정
- ServerPush
  - ex) a.html에 b.css, c.js가 있으면, a.html만보내주면 다시 또 요청할테니 미리 다 보내주는 것 
- Header Compression: 헤더의 크기를 줄여서 페이지 로드 시간 감소

### HTTP 3
**GOOGLE이 개발한 QUIC 이용

- UDP로 변경
- 보안강화
- IP가 아닌 Connection ID로 통신을 하여 IP가 바뀌어도 기존 연결을 유지할 수 있음
