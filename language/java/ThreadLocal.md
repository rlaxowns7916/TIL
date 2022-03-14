# ThreadLocal

**Java에서 제공하는 Class, Thread가 사용하는 지역변수** (java.lang.ThreadLocal)

- 두 Thread가 하나의 ThreadLocal변수를 참조하더라도, 서로의 ThreadLocal의 값은 다르다.
- Multi Thread환경에서 각 Thread마다 독립적인 변수를 가지고 활용 가능
- Thread 전용 전역저장소이다.
- 내부적으로 Thread정보를 Key로 가지는 Map으로 구성되어있다.

## 사용처

- SpringSecurity에서 사용자마다 다른 인증정보 & Session 정보
    - SecurityContextHolder 의 기본전략
- Thread-Safe한 데이터 저장
    - 동일한 ThreadLocal변수를 참조하더라도 다른 Thread에서는 서로의 변수를 볼 수 없다.
- MDC(Mapped Diagnositc Context)
    - log4j2,logback이 제공하는 Thread별 Log구별 도구
    - MultiClient환경에서 다른 Client와 값을 구별하고 로그추적 용이

## 사용법

1. ThreadLocal 객체를 생성한다.
2. ThreadLocal.set() 메서드를 이용해서 현재 쓰레드의 로컬 변수에 값을 저장한다.
3. ThreadLocal.get() 메서드를 이용해서 현재 쓰레드의 로컬 변수 값을 읽어온다.
4. ThreadLocal.remove() 메서드를 이용해서 현재 쓰레드의 로컬 변수 값을 삭제한다.
    - 사용이 완료된 값을 제대로 지우지 않으면, 재사용되는 Thread (ex: ThreadPoll)가 잘못된 값을 참조 할 수 도있다.

```java
public class ThreadLocalUser {
    private ThreadLocal<String> name = new ThreadLocal<>();

    public ThreadLocalUser(String user) {
        name.set(user);
    }

    public String getUser() {
        String user = name.get();
        name.remove();
        return user;
    }

}
```

## 코드 뜯어보기

### ThreadLocal
- ThreadLocalMap을 사용한다.
- 내부적으로 ThreadLocal을 Static Class로 갖고있다.

### ThreadLocalMap
- ThreadLocal Value를 저장하기 위해 **Hash Map을 커스터마이징** 한 것이다.
- 값을 저장하기 위해 Entry를 사용한다.
- Entry를 Static Class로 갖고있다.
- **각 Thread마다 ThreadLocalMap을 갖고있다.**

### 과정

#### 1. ThreadLocal 초기화
- 단순히 인스턴스가 생성된다.
- 초기화 되지 않는다.

#### 2. ThreadLocal -> set()
- getMap()을 통해서 ThreadLocalMap을 가져온다.<br>
![localSet](https://user-images.githubusercontent.com/57896918/158211117-2b615e72-2db3-4d66-aa93-4f783a2a44d1.png)


#### 3. ThreadLocal -> getMap()
- 현재 Thread가 갖고있는 ThreadLocalMap을 가져온다.<br>
![threadLocalGetMap](https://user-images.githubusercontent.com/57896918/158211079-0652818c-3dea-4026-9bee-3cd379864d36.png)

#### 4.1 ThreadLocalMap이 null 일 때
- 새롭게 map을 생성하면서 value를 통해 초기화한다.<br>
![createMap](https://user-images.githubusercontent.com/57896918/158211295-e28be26e-e24f-4548-9caa-613b42329409.png)


#### 4.2 ThreadLocalMap이 null이 아닐 때
- ThreadLocal을 Key값으로 value를 저장한다.
  - 왜 Thread가 아니라 ThreadLocal??
    - 한 Thread가 여러개의 ThreadLocal Value를 가질 수 있기 때문이다.<br>

![localMapSet](https://user-images.githubusercontent.com/57896918/158211411-97ef32aa-ebf6-4bb0-83e4-1c0ff66dd562.png)

#### 5 ThreadLocal get()
- 현재 Thread가 갖고있는 ThreadLocalMap을 가져온다.
- 내부적으로 저장되어있는 Entry를 타입 캐스팅하여 리턴한다.<br>

![get](https://user-images.githubusercontent.com/57896918/158211594-391012d3-3fcc-4b2f-b833-b8a75bd24c21.png)



#### 6. ThreadLocal remove()
- 현재 Thread가 갖고있는 ThreadLocalMap을 가져온다.
- null이 아닐 시, ThreadLocalMap 내부에있는 Entry들을 다 해제해준다.<br>

![remove](https://user-images.githubusercontent.com/57896918/158211626-9b64d858-fc3d-40d8-b4ea-5d29bfee2568.png)

