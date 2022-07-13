# Throwable
- Error와 Exception의 공통 조상 클래스
- 예외처리를 할 때 Throwable까지 처리를 해서는 안된다.

# Error
- java.lang.error 패키지
- 시스템에 비정상적인 상황이 발생 한 것
  - 시스템의 비정상 종료를 유발할 만큼 심각한 오류이다.
  - 복구 불가능
- 컴파일 시점에 에러발생 여부를 알 수 없다.
- 런타임 시점에 에러가 발생한다.
- ex) OutOfMemory, StackOverFlow

# Exception
- java.lang.exception 패키지
- 정상적인 프로램 실행에 어긋나는 것을 의미
- CheckedException은 컴파일 시점에 확인 가능, UncheckedException은 런타임에 확인가능
- try-catch를 통해 예방 가능
  - catch한 예외의 하위 Type까지 함께 처리된다.
- 자신이 처리를 하지 못하는 경우, throws를 통해서 자신을 호출한 측에 에러 처리를 미룬다.

## CheckedException
- Compile시점에 확인 가능
- 에러 처리를 하지 않으면 Compile불가
- IOException,ClassNotFoundException ...
  - RuntimeException을 제외한 Exception의 하위 클래스들이다.
- Exception 클래스는 CheckedException의 범주에 속한다.
  - Exception을 상속받으면 CheckedException이다.
  
## UnCheckedException
- RuntimeException을 상속
- 컴파일러가 예외를 체크하지 않는다.
  - 예외처리를 하지 않아도됨
  - throws를 생략해도 자동으로 던져진다.
- NullPointerException,ClassCastException...



## 예외 처리를 하지 못했을 때
1. Java Main Thread
  - 처리를 하지못한다면, 에러로그를 출력하면서 프로그램이 종료된다.
2. Web Application
  - 하나의 예외발생으로, 다수의 사용자가 사용하는 서버가 종료되어서는 안되기 때문에, WAS가 해당예외를 받아서 처리한다.
  - ErrorPage를 보여주는것이 일반적이다.
# 계층도
<img width="803" alt="스크린샷 2022-05-08 오후 4 40 47" src="https://user-images.githubusercontent.com/57896918/167286700-6594a62d-fbdb-4580-8c81-38c41534d58f.png">
