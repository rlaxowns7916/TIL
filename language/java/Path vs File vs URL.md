# Path vs File vs URL

## Path
- FileSystem에서 I/O 작업을 지원하는 API
- 실제 파일이아니라 **경로**를 의미한다.
- File 객체가 제공하는 모든 기능과 더 풍부한 기능을 제공한다.
  - path normalize 등등
  - Java 7 부터 제공
- 파일
- Thread-Safe

## File
- FileSystem에서 I/O 작업을 지원하는 API
- 실제 파일이아니라 **경로**를 의미한다.
- 레거시로 사용이 권장되지 않는다.
  - Java 시작부터 있었던 API
  - 오류처리의 부재
      - 응답에 대한 처리를 boolean으로 처리하는 API가 많다.
  - 심볼릭링크 미지원

## URL
- 리소스를 가리키는 구조화된 문자열이다.
  - 웹 URL만을 의미하지 않는다.
  - FTP, JAR, HTTP,File 등 다양하게 가능하다.
- Spring의 Resource 인터페이스는 URL을 추상화 한 것이다.