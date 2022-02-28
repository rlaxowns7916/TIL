# DomainModel
- 특정 도메인을 개념적으로 표현 한 것 이다.
- 도메인 용어를 적극적으로 담으려고 노력하자.
  - 적절한 도메인 단어를 찾는 것에 시간을 아끼지 말자. 
- 도메인마다 다루는 영역이 다르기 때문에, 같은 도메인 모델이 있을 수 있다.
- Entity와 ValueObject로 구분된다.
- **Setter를 사용하지 말자**
  - 객체 생성은 Setter가 아닌 생성자를 사용하고 생성시에 유효성을 검사한다.
  - 유효성 검사 또한 별도의 메소드로 빼는 것이 좋다.
  - 메소드 네이밍 또한 도메인 용어에 적절해야 한다.

## Entity (엔티티)
- 식별자를 갖는다.
- mutable한 객체이다.
- JPA의 @Entity와 상관이없다.


## ValueObject (값 객체)
- 개념적으로 완전한 하나를 포현한다.
- 뜻을 명확하게 하기위해서 사용할 때 도 있다.

```Java
class Money {
    int value;
    public Money(int value) {
        this.value = value;
    }
}

class Item {
    /**
     * price와 amount가 돈을 의미한다는 것이 더욱 명확해진다.
     */
    private Money price;
    private Money amount;
    private int quantity;
    private Product product;
}
```
- 식별자가 존재하지 않는다.
- Immutable하다 (값 수정이 아닌 객체 자체가 변경 되어야한다.)
- 동등성이 보장되어야한다.
- 생성될 때 유효성이 검증되어야한다.
- 엔티티의 복잡한 속성을 대신해서 사용된다.

### VO 사용의 장점
1. VO에 포함된 여러 속성들을 통해 복잡성을 줄일 수 있다.
2. Entity의 Logic복잡성이 낮아진다.
3. 올바르게 사용할 경우, 테스트 가능성, 동시성문제 해결, 확장성에서 유리한 점을 갖는다.
