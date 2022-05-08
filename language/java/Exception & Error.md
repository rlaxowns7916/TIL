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
- 정상적인 프로그램 실행에 어긋나는 것을 의미
- CheckedException은 컴파일 시점에 확인 가능, UncheckedException은 런타임에 확인가능
- try-catch를 통해 예방 가능


## CheckedException
- Compile시점에 확인 가능
- 에러 처리를 하지않으면 Compile불가
- IOException,ClassNotFoundException ...
- Exception 클래스는 CheckedException의 범주에 속한다.

## UnCheckedException
- RuntimeException을 상속
- 예외처리를 하지 않아도됨 
- NullPointerException,ClassCastException...


# 계층도
<img width="803" alt="스크린샷 2022-05-08 오후 4 40 47" src="https://user-images.githubusercontent.com/57896918/167286700-6594a62d-fbdb-4580-8c81-38c41534d58f.png">
