# inline 함수
- **함수를 호출하는 쪽에 함수본문을 붙여넣는다.**
- 함수 Call이 반복되어 호출되지 않기 때문에 성능상으로 유리한부분이 있다.
- **inline은 고차함수의 경우 인자로 받은 함수를 추측 할 수 있다면 인자로 받은 함수까지 inlining시켜버린다.**
  - 인자로 받은 고차함수에 **noinline** 키워드를 붙이면 inline을 강제로 막을 수 있다.
- non-local return (람다에서의 리턴) 사용이 가능해진다.
  - (조심) 기존의 return 과 같이, return하는 대상은 가장 가까운 함수이다 
- inline함수의 고차함수 파라미터가 non-local return을 방지하게 할 수 있다.
  - **crossinline**키워드를 사용하면 non-local return 사용을 금지 할 수 있다.

## 컴파일 전 후 비교
### 컴파일 전
```kotlin
inline fun add(num1: Int, num2: Int) = num1 + num2

fun main(){
    val num1 = 1
    val num2 = 2
    val result = add(num1,num2)
}
```

### 컴파일 후
```java
public static final void main(){
    int num1 = 1;
    int numm2 = 2;
    int var10000 = num1+num2; //inlining
}
```