# HTML (Hyper Text Markup Language)

1. Tag기반 언어
2. 현재버전은 HTML5
3. 넓은의미로는 HTML5 + CSS3 + ECMAscript + SVG + WebGL을 일컫는다.
4. 웹브라우저가 읽어서 렌더링한다.
5. HTML로 꾸밀순 있지만 CSS를 사용하도록한다.
## 문법

```HTML
<!DOCTYPE html> <!--문서의 타입이 HTML5라고 선언 ( 주석 )-->
<html>
    <head>
        <title>Hello HTML</title>
    </head>
    <body>
        <h1>HTML</h1>
        <a href="www.naver.com">링크</a>
    </body>
</html>
```
## 태그
***태그 안에 들어가는 속성은 key=value형식으로***
1. \<h1>~\<h6> : 제목요소를 크기별로 나열 (숫자가 클수록 글자크기 작아짐)
2. \<p> : 문단 태그
3. \<a> : anchor, href 속성과 함께 링크역할
4. \<img>: src속성과 함께 이미지를 나타낼 때사용
5. \<ul> : \<li>와 함께 번호가 있는 리스트
6. \<ol> : \<li>와 함께 번호가 없는 리스트
7. \<div>: 한 덩어리로 취급 (블럭 엘리먼트)
8. \<span>: 한 덩어리로 취급 (인라인 엘리먼트)
9. \<table>: 표를 나타냄, 
10. \<tr>: \<table>태그 안에들어가며, row 역할
11. \<th>: \<table>태그 안에들어가며, header역할
12. \<td>: \<table>태그 안에들어가며, column역할