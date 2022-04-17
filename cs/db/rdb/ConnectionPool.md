# Connection Pool
- DBCP(DataBaseConnectionPool)이라고 불린다.
- WAS가 미리 DB와 Connection을 해놓은 객체를 만들어 저장해 놓는 것이다.
- Client에게서 Request가 오면 Connection을 빌려준다.
  - 사용이 끝나면 Connection 객체를 다시 Pool에 반환한다.
- SpringBoot의 경우 **Hikari CP**를 default Connection Pool Library로 사용한다.
  - Spring 2.0 부터 이다.
  - 빠르고 가볍다.

## ConnectionPool의 이점
- 빠른 응답이 가능하다.
  - Connection을 미리 만들어 두었기 때문에, Client의 Request에 따른 DB접속 등의 로직을 빠르게 수행 가능하다.
- 서버의 부하를 줄여준다.
  - DB Connection을 맺는 과정은 서버에 많은 부하를 야기한다.
  - 미리 Connection을 만들고 재활용하는 과정을 통해서 서버의 부하를 줄일 수 있다.
- 한정적인 자원을 관리한다.
  - Connection이 들어올 때마다 무한정으로 Connection을 맺을수는 없다.
  - 한정된 수와 함께 재활용하기 때문에 자원관리에 용이하다.

## ConnectionPool 유의사항
- Connection의 개수가 한정적이기 떄문에, 많은 양의 동시 접근이 발생 할 경우 반납할 때 까지 기다려야 한다.
- Connection Pool의 개수가 크다면?
  - 사용자 대기시간은 줄지만, 리소스를 많이 사용한다.
- Connection Pool의 개수가 적다면?
  - 사용자 대기시간은 늘어나지만, 리소스를 적게 사용한다.
- 성능을 좌지우지 하는 것은 Connection Pool의 크기이다.

## Connection Pool & Thread
- WAS의 Thread는 Connection 작업만 하는 것은 아니다.
- Thread는 Connection Pool의 개수보다 여유가 있는 것이 좋다.
  - Connection의 개수가 더 많다면, 공간만 차지하고 놀게된다.
- Spring은 아래와 같은 Deafault 설정을 갖는다.
  - ThreadPool : 200
  - ConnectionPool : 10
