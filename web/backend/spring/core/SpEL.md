# SpEL (SpringExpressionLanguage)
- Spring3.0부터 지원
- 객체 그래프 조회 및 조작 기능 제공

## 문법
- #{"표헌식"}
  - 표현식 평가 및 실행
- ${"프로퍼티"}
  - 표현식은 프로퍼티를 가잘 수 있다.
  - #{${app.port}+1}
- 독립적으로 사용할 때는 Parser와 Expression을 사용
```java
ExpressionParser parser = new SpelExpressionParser();
Expression expression = parser.parseExpression("SpEL표현식");
Strin value = expression.getValue(String.class);
```
**주로 @Value와 함께 사용된다.**
## 지원기능
1. 리터럴 표현식
```java
Expression exp = parser.parseExpression("'Hello World'.concat('!')");
```
2. Boolean, 관계연산자
```java
Expession exp = parser.parseExpression("name == 'Kim'");

@Value("#{1 eq 1}")
private boolean isEqual;
```
3. 산술 연산자
``` java
@Value("#{36 div 2}")
private doule divideNum;

@Value("#{2^10}")
private dobule power;

```
4. 정규표현식
```java
@Value("#{'100' matches '\\d+' }") // true
private boolean validNumericStringResult;
```
5. 클래스 표현식
6. 프로퍼티,배열,리스트,맵에 대한 접근 표현
```java
Expression exp - parser.parseExpression("'person.name'");

@Value("#{ systemProperties['user.region'] }")
private String defaultLocale;
```
7. 생성자 호출
```java
Expression exp = parser.parseExperssion("new String('hello World').toUpperCase()");
```
8. Bean참조
```java
 @Value("#{sample.value}")
 int value //sample이라는 Bean의 value
```
9. 삼항 연산자
```java
@Value("#{2 > 1 ? 'a' : 'b'}")
private String value;
```
10. 변수
11. 사용자 정의 함수
