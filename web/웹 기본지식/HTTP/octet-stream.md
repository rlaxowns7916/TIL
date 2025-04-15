# octet-stream
- 8bit 단위로 이루어진 binary-data를 나타내는 MIME 타입
- 특정한 형식 (MP3, PNG, JSON, ...)으로 정의되지 않은 일반적인 이진 파일을 의미한다.

| 항목 | 설명                                                |
|------|---------------------------------------------------|
| MIME 타입 | `application/octet-stream`                        |
| 의미 | 형식이 정의되지 않은 **순수한 바이너리 스트림**                      |
| 파일 종류 | 텍스트가 아닌 모든 파일 가능 (EXE, PDF, ZIP, MP4 등)           |
| 콘텐츠 해석 | 클라이언트가 직접 해석 방법을 알아야 함 (혹은 파일 확장자 기반으로 유추) <br/> 브라우저에서는, 다운로드 유도로 전략을 취한다. |
| 전송 방식 | 보통 `Content-Disposition: attachment` 헤더와 함께 사용됨   |


```http
HTTP/1.1 200 OK
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="report.pdf"
Content-Length: 150240

(바이너리 데이터)
```
- content-disposition 헤더를 통해서, 브라우저가 파일을 다운로드하도록 유도한다.
- filename 파라미터를 통해서, 다운로드할 파일의 이름을 지정할 수 있다.