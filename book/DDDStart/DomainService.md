# Domain Service
- 한 Aggregate에서 기능이 불가능한 경우, DomainService에서 구현한다.
- 다른 Domain요소와 같은 패키지에 위치한다.
  - 외부 기술에 의존하고 있다면 Interface로 뺀 다음, 구현체를 Infrastrcutre에서 구현한다. 
  - Infrastructure Layer에 위치하는 DomainService 구현체는 외부시스템과의 연동, Domain Model간의 변환을 처리한다.
### 예제
1. 상품 Aggregate: 구매하는 상품의 가격이 필요하고, 상품에 따라 배송비를 추가해야 한다.
2. 주문 Aggregate: 상품별로 구매 개수가 필요하다.
3. 쿠폰 Aggregate: 쿠폰별로 지정한 할인금액이나, 비율에 따라 주문 총금액에서 할인한다.
4. 회원 Aggregate: 회원등급에따라 추가할인이 가능해진다.

**어느 Aggregate에서 결제로직을 진행해야할까?**

- 한 Aggregate에 넣기 애매한 Domain개념을 구현하면, 코드가 길어지고 외부의존이 높아진다.
- Aggregate의 범위를 넘어서는 구현은 Aggregate를 한눈에 파악하기 힘들게 만든다.

## DomainService
- Domain의 상태관리가 아닌, 단순히 로직만을 구현한다.
- Domain의 의미가드러나는 용어와 타입을 Service이름으로 짓는다.
- DomainService를 사용하는 주체는 Application영역과,Aggregate가 될 수 있다.

#### 사용 예시
```java
public class OrderService{
    private DiscountCalculationService discountCalculationService;
    
    @Transactional
    public OrderNo placeOrder(OrderRequest orderRequest){
        OrderNo orderNo = orderRepository.nextId();
        Order order = createOrder(orderNo,orderRequest);
        orderRepository.save(order);
        
        return orderNo;
    }
    
    private Order createOrder(OrderNo orderNo, OrderRequest orderReq){
        Member member = findMebmer(orderReq.getOrderId());
        Order order = new Order(orderNo...);

        /**
         *  Domain에 DomainService를 넘겨줌
         */
        order.calculateAmounts(this.discountCalculationService,member.getGrade())''
    }
}
```
- Domain에게 DomainService를 넘기는 책임은 Application영역에 있다.
- 하지만 Domain영역에 DomainService를 파라미터, 혹은 DI시키는 것은 의존성을 높이기 떄문에 추천하지 않는다.
- 차라리 다른 Aggregate를 인자로 넘기자.

