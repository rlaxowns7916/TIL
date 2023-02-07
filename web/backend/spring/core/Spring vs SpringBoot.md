# Spring
- Java 기반 WebApplication 구축을 위한 오픈소스 Framework
- IOC,DI,PSA 등의 기능을 통해서 만들어진 경량화 웹 프레임워크
- XML설정을 필요로한다.
- DI등의 개념이 포함된 여러가지 모듈로 구성되어 있다.
  - Spring JDBC
  - Spring MVC
  - Spring Security
  - Spring AOP


# SpringBoot
- SpringFramework의 확장이다.
  - SpringFramework를 통한 더빠른 개발을 목표로 한다.
  - **Spring을 기반으로** 실무 환경에 사용 가능한 수준의 독립실행형 어플리케이션을    
    복잡한 고민없이 빠르게 작성 할 수 있게 도와주는 도구 집합이다.
- Starter 의존성을 통해서 의존성 세팅을 간소화 해준다.
- 내장 웹서버 기능을 제공한다.
- In-Memory 데이터베이스 기능을 제공한다.
- AutoConfiguration이 주요한 기능이다.
  - Spring의 많은 환경설정을 자동으로 설정해준다.
  - XML 설정이 필요없다.

## 등장 배경
- 알아야 하는 사전 지식이 너무많았다.
  - 많은 설정 파일
  - Container 지식 (WebServer | Servlet Container)
    - Tomcat이 아니라 다른걸 쓴다면? 그 때마다 새로운 지식이 필요
- SpringBoot의 생성이 결정된 가장 큰 이유는 ContainerLess이다.
  - 내장 Tomcat이 그렇기에, 가장 큰 특징이 되었다.

## 특성 - Opinionated (주장이 강한)
- SpringBoot가 일단 다 정한다. 
  - 기존의 Spring은 다양한 선택지를 제공했다.
- 개발자는 정해진대로 개발만 하면 된다.
  - 가장 일반적인 설정 값을 제공한다.
  - **Spring 생태계 프로젝트, 표준 자바 기술, 오픈소스 기술 들과 그에따른 의존관계, DI에 대한 기본 설정을 제공한다.**
  - Spring 비해서 기술의 선택, 설정 등의 고민을 줄일 수 있게 되었다.