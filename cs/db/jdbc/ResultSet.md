# ResultSet
- JDBC에서 SQL 실행결과를 가져오는 객체
- SELECT 쿼리의 결과가 순서대로 저장된다.
- rs.next()로 Cursor를 움직여서 결과로 얻은 Row를 순회 할 수 있다.

```java
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.NoSuchElementException;

public class Example {
    public static void main(String[] args) {
        String sql = "SELECT * FROM member WHERE member_id = ?;

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery(); //데이터 변경이 아닌 조회의 경우 executeQuery()

            /**
             * 최초의 next는 데이터 유무를 확인한다.
             *
             */
            if (rs.next()) {
                Member member = new Member();
                member.setInt(rs.getString("member_id"));
                member.setName(rs.getString("name"));
                return member;
            } else
                throw new NoSuchElementException("Member Not Found");
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
```