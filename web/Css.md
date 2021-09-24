# CSS(Cascaded Style Sheet)
**페이지의 내용과 스타일을 분리**<br>
**HTML 3부터 분리됨**
**계층적으로 적용(우선순위가 있다. 구체적인 것 > 포괄적인 것)**
## 적용 방법
1. HTML파일 내부의 head 태그 사이에 \<style>\</style>를 통해서 css 작성
2. 별도의 .css파일을 만들고, \<link href="css경로" rel="stylesheet" type="text/css/>
3. 태그마닫 style 속성을 통해 작성
4. 브라우저 default

**우선순위: style속성 > \<style> > .css > browser default**

***
## 구조 
**Selector {property : value; ... property:value;}**

1. Selector의 종류에는 tag, class(.), id(#) 가 있다.
2. 중괄호({ })를 통해서 구분된다.
3. property-value 쌍은 세미콜론(;)를 통해서 구분된다.
4. 중복되는 속성일 경우 콤마(,) 를 통해서 여러 Selector 포함 시킬 수 있다.
5. .(dot) 을 통해서 하위 Selector까지 선택 할 수 있다.(p.center --> center class를 가진 p 태그)

### Selector
- tag: HTML의 tag 
- id : #를 통해서 구분, 특정 하나를 지정해서 적용
- class: .를 사용, 같은 클래스를 가진 값에 스타일 일괄적용 
***
```html
<!DOCTYPE html>
<html>
    <head>
        <style>
            #sample1 {
                color: red;
                text-align: center;   
            }       
            .sample2{
                 color: aqua;
            }   
        </style>
    </head>
    <body>
        <p id="sample1"> id selector 본문</p>
        <h1 class="sample2">class selector h1</h1>
        <h2 class="sample2">class selector h2</h2>    
    </body>
</html>
```
***
### 색상
1. 색상 이름 지정 (140개): color : black;
2. 색상 모드
    - RGB: (0,255,255) -- 0~255까지의 숫자 가능 
        - rgba: Transparency(투명도) & Opacity(불투명도) //alpha 채널이라고도 부른다.
    - HEX: #ff0000 -- 16진수로(2자리 씩)
    - HSL(Hue - Saturation - Lightness) : 색조, 채도, 밝기 지정 