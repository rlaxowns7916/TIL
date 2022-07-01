# Query
- 실행방식은 PrepareStatement, Statement 2가지 방식이 있다.
- 과정
  1. 쿼리문장 분석
  2. 컴파일
  3. 실행

## DB에서의 SQL문 실행과정
1. parse
2. bind
3. execute
4. fetch
```java
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Example {
    public static void main(String[] args) {
        String sql = "INSERT INTO member (member_id,name) values (?,?);";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, 1);
            pstmt.setString(2, "name");
            pstmt.executeUpdate(); //영향받은 Row 수
            
            pstmt.set
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        } finally {
            close();
        }
    }

    private void close(Connection conn, Statement stmt, Resultset rs) {
        /**
         * try-catch-finally를 사용할 수 도 있지만,
         * 앞부분에서 오류가 발생하면 뒷부분이 실행되지 않는 문제가 있다.(finally 내부에서)
         * 그렇기 때문에 따로 메소드를 빼서 처리한다.
         * 
         * catch에서 별도의 처리를 해주지 않는 이유는
         * 따로 해결책이 없기 때문이다.
         * 
         * 뒷부분의 리소스 정리에 영향을 끼치는 것을 막기위한 코드이다.
         */
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
```
1. Connection 맺기
2. PrepareStatement || Statement를 사용해서 Query작성
3. ResultSet으로 결과 받기
4. 자원 후처리 하기 


## 2. PrepareStatemen
- 캐시를 사용한다.
    - 최초 실행시점에 쿼리 분석 이후 캐시에 담아서 재사용한다.
    - 쿼리가 자주사용할 때 주로사용한다.
- SQL Injection에 대응이 가능하다.
  - 최초 실행시에만 parse ~ fetch를 수행하며, 그 이후부터는 bind부터 수행한다.
  - bind 부분의 변수값을 인터프리터나 컴파일 언어로 사용하기 때문에, 문법적 의미를 가질 수 없으며, 단순 값으로 취급된다.
## 3. Statement
- 캐시를 사용하지 않는다..
  - 매번 쿼리 분석 및 컴파일을 수행한다.
- Dynamic SQL에서 사용한다. (SQL에서의 분기)
  - 캐시의 장점이 사라져서 오히려 오버헤드가 생기기 때문이다.
- SQL Injection 공격에 취약하다.
  - parse~fetch까지의 과정을 매번 수행한다.