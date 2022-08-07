# Query
- 실행방식은 PrepareStatement, Statement 2가지 방식이 있다.
- 과정
  1. 쿼리문장 분석
  2. 컴파일
  3. 실행

## DB에서의 SQL문 실행과정
1. parse
  - 문법 검사
  - 권한검사
  - 실행계획 탐색 혹은 생성
2. bind
  - 넘어온 값을 치환
3. execute
  - 실행
4. fetch
  - 실행한 결과 User Process에 전달
```java
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class Example {
    public static void main(String[] args) {
        String sql = "INSERT INTO member (member_id,name) values (?,?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, 1);
            pstmt.setString(2, "name");
            pstmt.executeUpdate(); //데이터를 변경할 때 사용하는 메소드, 리턴 값은 영향받은 Row의 수
            
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
- DDL에 적합하다.
- Query에 인자를 부여할 수 없다.
- 캐시를 사용하지 않는다..
  - 매번 쿼리 분석 및 컴파일을 수행한다.
- SQL Injection 공격에 취약하다.
  - parse~fetch까지의 과정을 매번 수행한다.
- Dynamic SQL에서 사용한다. (SQL에서의 분기)
  - 캐시의 장점이 사라져서 오히려 오버헤드가 생기기 때문이다.
```java
public List<Book> findAllFiltered(String title, String genre, String author) {
        Connection connection = jdbConnectionWrapper.getConnection();
        List<Book> books = new ArrayList<>();
        try {
            String query = "SELECT * FROM book";
            if (!StringUtils.isNullOrEmpty(title) ||
                    !StringUtils.isNullOrEmpty(genre) ||
                    !StringUtils.isNullOrEmpty(author)) {
                query = query + " WHERE";
            }

            if (!StringUtils.isNullOrEmpty(title)) {
                query = query + " title LIKE '%" + title + "%'";
            }

            if (!StringUtils.isNullOrEmpty(genre)) {
                if (!StringUtils.isNullOrEmpty(title)) {
                    query = query + " AND";
                }
                query = query + " genre LIKE '%" + genre + "%'";
            }

            if (!StringUtils.isNullOrEmpty(author)) {
                if (!StringUtils.isNullOrEmpty(title) ||
                        !StringUtils.isNullOrEmpty(genre)) {
                    query = query + " AND";
                }
                query = query + " author LIKE '%" + author + "%'";
            }

            System.out.println(query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            //some extra code here for retrieveing data
```