# Dom(Document Object Model)
1. 파일을 객체로 대응해 인식
2. HTML/XML등을 트리 형태로 처리하는 방식
3. 문서 전체 구조를 파악하여 변경(느림)
4. 파일 다운 -> 파싱 -> DOM트리로 변환


## Dom 구조



***마지막(leaf)는 텍스트이다.***

### JS Dom접근
```js
    document.getElementById("id") //id는 유일한 값
    document.getElementsByTagName("h1") //태그는 여러개일 수 있기 때문에 복수
    document.getElementsByClassName("class") //클래스에 여러개가 속할 수 있기 때문에 복수
```
**innerHTML(HTML안의 텍스트 접근)은 DOM API가 아니다.<br>
document.getElementById("id").childNodes[0].nodeValue ---> (Dom)**

1. 노드 추가하기
```js
const newTagNode = document.createElement("p");
const newTextNode = document.createTextNode("textNode");
newTagNode.appendChild(newTextNode);
```
2. 노드 수정하기
```js
const parent = document.getElementById("id1")
const child = parent.childNodes[0]
const newChild = document.createElement("p")
const newChildText = document.createTextNode("hello")

newChild.appendChild(newChildText)
parent.replaceChild(newChild,child)
```
3. 노드 삭제하기
```js
document.getElementById("id").remove()
```

## JavaScript Framework
**DOM 조작이 느리고 시스템 부하가 있기 떄문에 성능 및 편의성 개선을 위해 출현**<br>
**DOM이 일부만 변경되어도 다시 다 리랜더링 하기 때문**

VirtualDom : 특정 부분만 리랜더링  

1. Angular(Google)
2. React.js(Facebook)
3. Vue.js
4. Node.js (Servier Side // v8엔진)


