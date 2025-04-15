# multipart/form-data
- 하나의 HTTP요청에 내부 여러 파트(part)를 구분하여 전송하는 방식
- 여러 파일 및 폼 필드가 각각의 part로 나뉘어 단일 커넥션으로 서버에 전송된다.
  - Boundary라는 문자열을 기준으로 구분된다.

```http request
POST /upload HTTP/1.1
Host: example.com
Content-Type: multipart/form-data; boundary=boundary123

--boundary123
Content-Disposition: form-data; name="file1"; filename="foo.png"
Content-Type: image/png

(파일1 바이너리 데이터)
--boundary123
Content-Disposition: form-data; name="file2"; filename="bar.jpg"
Content-Type: image/jpeg

(파일2 바이너리 데이터)
--boundary123
Content-Disposition: form-data; name="description"

파일에 대한 설명
--boundary123--
```

## 구성요소

### [1] Content-Type
- multipart/form-data; boundary=----boundaryString 형태로 표시
- boundary 파라미터는 요청 바디에서 각 파트를 나누는 구분자 역할
- **내부적인 part들은 개별 content-type을 가질 수 있다.**

### [2] Boundary
- 각 파트를 구분하는 문자열
- 시작 시, boundary 앞에 --가 붙고, 끝날 때는 --가 붙는다.