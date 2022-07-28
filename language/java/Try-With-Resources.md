# Try-With-Resources

- Java7에 처음 도입된 기능
- try에 선언된 객체들이 종료될 때 자동으로 Resources를 해제하는 기능
- AutoCloseable을 구현하였따면, 객체의 close() 메소드를 자동으로 호출해준다.
- finally 블록을 사용하지 않을 수 있게 되었다.

## 예제 (try-catch-finally)

```java
import java.io.BufferedInputStream;
import java.io.FileInputStream;

class Example {
    public static void main(String[] args) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream("example.txt");
            bis = new BufferedInputStream(fis);

            /**
             * 로직의 수행
             */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```

- finally는 주로 Resource 해제를 하는데 사용이 되었다.
- finally에서 Resource를 해제 할 떄 또다른 try구문이 필요하기 떄문에 코드의 양이길어지고 depth또한 깊어진다.

## 예제 (try-with-resources)

```java
import java.io.BufferedInputStream;
import java.io.FileInputStream;

class Example {
    public static void main(String[] args) {
        try (
                FileInputStream fis = new FileInputStream("example.txt");
                BufferedInputStream bis = new BufferedInputStream(fis);
        ) {
            /**
             * 로직의 수행
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
- try를 벗어나거나, try구문에서 예외가 발생하면 어떻게 됐든 무조건 Resource를 close한다.
- 명시적으로 close()를 호출하지 않아도 된다.
- 훨씬 가독성에서 좋다.

## AutoCloseable
- try-with-resources가 모든 객체를 Close해주지 않는다.
- AutoCloseable Interface를 구현한 객체가 Close가능하다.
- Override를 통해서 Logging등을 구현 할 수 있다.
- **Close Method 구현시 구체적인 Exception을 Throw 할 것을 권고 한다.**
  - 실패 할리가 없다면, Exception을 던지지 않아도 된다.
  - close()에서 에러가 발생할 수 있다면, InterruptException을 던지는 것을 권고한다.
    - Thread의 Interrupt 상태와 상호작용하기 때문에, 억제되었을 때 Runtime에 문제를 일으킬 수 있다.
```java
public interface AutoCloseable {
    void close() throws Exception;
}
```
