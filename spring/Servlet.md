# Servlet
**Java로 만들어진 동적인 웹페이지를 만들어 주기 위한 인터페이스**
- Request 당 쓰레드 생성
- Singleton으로 관리
- CGI에서 발전

## Servlet 생명주기
1. 객체생성
2. init(): 초기화
3. service(): doGet(),doPost() ...
4. destroy(): 소멸

## CGI (Common Gateway Interface)
- 웹서버에서 동적인 컨텐츠를 제공하기 위한 규약(인터페이스)
- 다양한 언어로 정의 가능
- Request가 들어올 때 마다 Process 생성
- 요청이 종료되면 메모리에서 제거된다.


## ServletContainer
- TomCat
- Servlet을 담는 상자(Servlet의 생명주기를 관리)
- MultiThreading 관리
- 보안관리
- 웹서버와의 통신기능 제공 (소켓 생성 및 listen(), accept() 등 제공)
- **URL에 따른 올바른 서블릿에 요청을 매핑해주고, 생성된 결과를 올바른 곳에 전달 해주는 역할**


## DispatcherServlet
- SpringMVC에서 사용
- FrontController패턴으로 구현 (모든 요청을 받는 하나의 서블릿이 앞단에 위치)
- 들어오는 모든 요청 Handling 및 Handler 매핑 (일일이 web.xml에 설정해주지 않아도된다.)
- 공통작업 처리


![dispatcherServlet](https://user-images.githubusercontent.com/57896918/147087215-820ecfbc-82d9-4fe3-8ba0-641fbd4b047e.png)
