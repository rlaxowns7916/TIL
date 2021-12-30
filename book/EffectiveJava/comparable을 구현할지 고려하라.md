# Comparable을 구현할지 고려하라

Comparable을 구현하게되면 된다면 Java에서 제공하는 정렬기능을 사용 가능하다.

Java 라이브러리의 모든 값 클래스와 열거형은 Comparable을 구현하였다.

```java
public interface Comparable<T>{
		int compareTo(T t);
}
```

### compareTo메소드 규약

```markup
compareTo메소드는 객체와 주어진 객체의 순서를 비교한다.

**이(this)객체가 주어진 객체보다 작다면 -1, 같다면 0, 크다면 1을 리턴한다.

비교할 수 없는 타입의 객체라면 ClassCastException을 리턴한다.

1. Comparable 인터페이스를 정의한 모든 클래스는 
   sgn(x.compareTo(y) == - sgn(y.compareTo(x))를 만족해야한다. (예외 또한 같이 던져야한다)

2. 추이성을 보장해야한다. x.compareTo(y) > 0 && y.compareTo(z) > 0 이라면,
	 x.compareTo(z) 또한 만족해야한다.

3. x.compareTo(y) == 0 일 때, sgn(x.compareTo(z)) == sgn(y.compareTo(z)) 이다.

4. x.compareTo(y) == 0이 라면, x.equals(y) 또한 true를 리턴하는 것이 좋다. (Optional) 
   지키지않아도 동작하지만, 오작동 할 수 있다.**
```

hashcode 규약을 지키지못하면, hash를 사용하는 클래스(HashMap,HashSet ..)를 사용하지 못하듯이,

compareTo규약을 지킨다면, 비교를 활용하는 클래스들과 함께 사용되지 못한다.

주의점도 같다. compareTo메소드를 확장한 클래스에 compareTo를 구현하고자한다면,

상속(Inheritance)이아닌 합성(Composition)이 좋다.

### Comparator

Comparable과 유사하게 정렬기준을 제공해주지만, 표준이 아닌 순서가 필요할 때 사용된다.

```java
import static java.util.Comparator.comparingInt;

private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);
```

### 작성요령

Comparable 인터페이스는 equals와 작성요령은 비슷하지만,  타입을 인수로 받는 제네릭 인터페이스이므로, compareTo 메서드의 인수타입은 컴파일 타임에 정해진다.

**즉, 입력인수의 타입을 확인하거나 형변환을 할 필요가 없다.**

Java 7버전부터 Wrapper Class들이 제공해주는 메소드를 사용하는 것이 좋다.

```java
public int compareTo(PhoneNumber pn){
   int result = Short.compare(areaCode,pn.areaCode);
   if(result == 0){
       result = Short.compare(prefix,pn.prefix);
       if(result == 0)
          result = Short.compare(lineNum,pn.lineNum);
	  }
    return result;
}
```

물론 간결하지만, 성능상의 저하가 뒤따른다.