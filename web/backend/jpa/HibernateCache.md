# Hibernate Cache

## 캐시의 종류

### 1. FirstLevelCache
- 기본적으로 Enable이다.
  - Disable 할 수 없다. (Hibernate가 자체적으로 Enable 한 것)
- Session에 저장된다.
- 같은 Persistence Context 에서 식별자를 기준으로 같은 값을 리턴한다.
- 1차 캐시에서 먼저 Entity를 조회한다.
  - 1차캐시에 없으면 DB에서 조회하고, 조회해온 것을 1차캐시에 저장한다.
- 1차 캐시는 드라마틱하게 DB접근 횟수를 줄이지는 못한다.


### 2. SecondLevelCache
- ApplicationLayer의 Cache이다.
  - Memory에 Cache하는 것이다.
- 기본적으로 Disable 이다.
- SessionFactory 에 저장하여, 모든 어플리케이션의 세션에서 접근이 가능하다.
  - SessionFactory가 종료되면, 그 안에 있던 모든 내용물도 삭제된다.
- 1차캐시에 검색 후, 없을 시 2차캐시에서 검색한다.
  - 2차캐시에 존재하지 않을 경우 DB에서 조회하고 2차캐시에 등록한다.
  - 존재한다면 2차캐시에 존재하던 Entity의 복사본을 리턴한다.
    - 동시성을 극대화하기 위해서, 원본을 리턴하지 않는 것이다.
    - 같은 객체를 동시에 수정한다면 동시성문제가 발생할 수 있기 떄문이다.
    - Lock을 사용하는 것보다 복사본을 리턴하는 것이 훨씬 저렴하다.
- Hibernate는 2차캐시 구현체를 제공해주지 않는다. 
  - CacheProvider라는 Interface를 제공해준다.
  - CacheProvider를 구현하는  Third-Party라이브러리를 통해서 구현가능하다.
- Heap영역에 존재한다.(DB보다 빠른 이유)

#### 실제 사용하는가?
- 구현이 복잡하다,.
- Entity 단위의 Cache이다. (굳이 Entity단위로 할 필요없다.)
- 지원해주는 Library의 종류가 적다.


## CacheConcurrencyStrategy
![CacheConcurrencyStarategy](https://user-images.githubusercontent.com/57896918/159160348-89d8333c-4852-40c3-85f5-2aefd3c473a5.png)

## CacheMode
![캐시모드](https://user-images.githubusercontent.com/57896918/159160366-63327cd2-3620-4ded-a14a-dbb9aac05620.png)


## 2차 캐시 구현체
### 1. HashTable
### 2. EHCache
- 로컬 캐시
- Spring 내부적으로 동작한다. (Daemon을 가지고 있지 않다.)
- 3버전부터 JSR-107(JCache=표준) 와의 호환성을 제공한다.
- Write-through이 default 전략이다.
### 3. Infinispan
- Java와 Scala로 구현됨
- Library모드 (Embedded,Java만 지원), Client/Server모드 두가지 모두 지원한다.


## 참고자료
- https://cla9.tistory.com/100
- https://www.ehcache.org/documentation/3.4/getting-started.html#configuring-ehcache
- https://www.baeldung.com/hibernate-second-level-cache
