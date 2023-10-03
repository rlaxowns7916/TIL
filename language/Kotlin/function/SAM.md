# SAM (Single Abstract Method)
- 추상 Method가 하나만 있는 것을 의미한다.
  - Java의 Runnable (대표적)
- 추상 Method하나만 가진 Interface를 SAM Interface라고 부른다.
  - Kotlin에서 SAM Interface를 선언하려면 앞에 fun을 붙여야한다.
    - ```kotlin
        fun interface MySamInterface
      ```

## 람다의 인스턴스화 (SAM 생성자)
- Java에서는 SAM의 인스턴스화가 가능하다.
- Kotlin에서는 SAM 생성자를 통해서 인스턴스화가 가능하다.
  - SAM 이름 + 람다식
  - 함수 Parameter의 경우 생략이 가능하다.
    - 하지만 암시적인 호출은 의도치않은 SAM이 호출 될 수 있기 때문에 명시적으로 SAM 생성자를 사용하는 것이 좋다.
### Java - 람다 인스턴스화
```java
class Main{
    public static void main(String[] args) {
        StringUpper upper = s -> s.toUpperCase();
    }
}
```

### Kotlin - 람다 인스턴스화
```kotlin
fun main(){
    val upper = StringUpper { s -> s.toUpperCase()}
}
```