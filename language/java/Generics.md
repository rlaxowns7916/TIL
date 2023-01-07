# Generics (제네릭)
- Java 5부터 도입
- Compile 시의 타입 체크(compile-time type check)를 해주는 기능이다.
  - 타입 안정성이 상승한다.
  - 재사용성이 증가한다.
  - 형변환의 번거로움에서 벗어 날 수 있다.
- Generic은 Class, Interface, Method, Collection 에 선언할 수 있다.

## Generic 생성 제한
1. Static 변수
    - Generic Type (T)는 Instance Type으로 간주된다.
    - Static 변수는 최초에 Memory에 올라가는 시점 이후에 변하지 않기 때문에, static 변수로의 선언은 불가능하다.
    - **(Static Method는 가능하다)**
2. T[] (array) 생성
    - new 로 생성하는 시점에 정확한 Type을 알아야 하기 때문이다.

## 예시
```java
class Box<T> {
  private T item;
  
  public T getItem(){
    return item;
  }
  
  public void setItem(T item){
    this.item = item;
  }
  
  public static <T> List<T> fromArrayToList(T[] a) {
    return Arrays.stream(a).collect(Collectors.toList());
  }
}
```
- Static Method에도 Type을 지정 해 줄 수 있다.
- **Class의 TypeParameter와 다르다**
  - Method의 TypeParameter가 우선시된다.


## 공변 vs 무공변 vs 반공변
```text
내용이 어려우니,

PECS (Producer - Extends & Consumer - Super)를 우선적으로 기억하자.
해당 제네릭 타입이 Producer(제공자) 역할이 된다면, extends를 사용하자.
해당 제네릭 타입이 Consumer(소비자) 역할이라면 (다른 메소드를 호출 등등) super를 사용하자.
```
- 배열은 공변이다.
  - RuntimeException을 막을 수 없다.
  - 제한적 와일드 카드를 통한 Generic은 그것을 막아준다.

### [1] 무공변
- A가 B의 상위타입일 때, C\<A>는 C\<B>의 상위 타입이 성립하지 않는다.
```java
public class Example {

  public static void main(String[] args) {
    Fruit a = new Apple(); //OK
    Delivery delivery = new Delivery();
    
   Box<Fruit> fruits = new Box();
   Box<Apple> apples = new Box();
   
   apples.add(new Apple());
   apples.add(new Apple());

    /**
     * 동작 하지 않는다.
     * 
     * 무공변에 따라서,
     * Box<Fruit>은 Box<Apple>의 상위 타입이 아니기 때문이다.>
     */
   delivery.doDelivery(apples,"Taejun");
  }

}

class Delivery{
  public void doDelivery(Box<Fruit> friuts, String customer){
    /**
     * 고객에게 Fruit Box를 전달
     */
  }
}

class Box<T> {

  private List<T> fruits;

  public void add(T fruit) {
    this.fruits.add(fruit);
  }
}
class Fruit{
  
}

class Apple extends Fruit{

}
```

### [2] 공변
- A가 B의 상위타입일 때, C\<A> 또한 C\<B>의 상위타입이다.
- Java에서는 기본적으로 무공변이다.
    - **? extends T**를 통해서 공변하게 만들 수 있다.
- 공변 타입이 Consumer의 입장일 때는 **Compile Error가 뜬다.**
  - 공변타입의 Method 인자로 실제 어떤 타입이 들어왔는지 추론 할 수 없기 떄문이다. 
  - 공변이 해결한 문제는 **누군가가 공변타입을 호출 할 때의 상위 하위타입간의 호환성 이다.**
- **Producer 역할 일 때, Extends 해야 한다.**
- 해당 객체의 하위타입만을 허용한다. (UpperBound - 해당 타입의 부몬타입들은 들어 올 수 없다.)
- **읽을 수는 있으나, 쓸 수 는 없다**
```java
class Delivery{
  public void doDelivery(Box<? extends Fruit> friuts, String customer){
    /**
     * 고객에게 Fruit Box를 전달
     */
  }
}
```
#### Read의 관점
- UpperBound는 읽을 수 있다.
  - UpperBound로는 모두 형변환이 가능하기 때문이다.
- 그 하위타입들은 읽을 수 없다.
  - 하위타입의 어떤 것이 들어올 지 예상 할 수 없기 때문이다.

#### Write의 관점
- 아무 것도 할 수 없다.
- 실제 객체 값에 대한 추론이 불가능하다.
  - 실 객체가 subType인데, UpperBound가 들어갈 수도 있다.
  - 1 parent, 2 child (Sibling) 관계 일 때, sibling관계끼리 엮일 수도 있다.



### [3] 반공변
- **A가 B의 상위 타입일 때, C\<A>가 C\<B>의 하위타입이면 반공변이라고한다.**
  - 제네릭에 해당하는 객체의 관계와 반대가 된다.
- ? super T 형태를 통해서 반공변을 만들어 낼 수 있다.
  - Consumer일 때 사용한다.
  - 파라미터로 넘어오는 타입의 최소치를 알 수 있기 떄문이다.
- 해당 객체의 상위타입만을 허용한다.
- 쓸 수 는 있지만 읽을 수는 없다.
```java
class Example{
  public static void copy(List<? extends Number> source,
      List<? super Number> destination) {
    for(Number number : source) {
      destination.add(number);
    }
  }
}
```

#### Read의 관점
- 어떤 부분에서 타입추론을 해야할지 모르기 때문에 읽을 수 없다.
  - 맨 상위인 object를 읽을 것인가? (Object로 뭘할건데...)

#### Write의 관점
- 자식은 부모에 대한 모든 것을 갖고있기 때문에, LowerBound에 대한 write는 가능하다.
  - 나머지 타입들에 대한 확실한 추론이 불가능하리 때문이다.
  - 상위 타입을 Insert했는데 실제 타입이 LowerBound라면? --> RuntimeException
