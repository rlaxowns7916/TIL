# Aggregate
- 관련 객체들을 하나로 묶은 군집
- 도메인의 복잡도를 감소 시킨다.
- Aggregate Root 객체를 통해서 하위 Entity, Vo에 접근가능
  - 캡슐화가 가능해진다.
- 한 Aggregate에 속한 객체는 다른 Aggregate에 속하지 않는다.
- 한 Aggregate는 다른 Aggregate를 관리하지 않는다.

#### Aggregate를 묶을 때 주의할 점
1. A -> B 관계라고 A와 B가 같은 Aggregate라고 볼 수 없다.
2. Aggregate에 속한 객체들은 대부분 같은 생명주기를 공유한다.
3. Aggregate에는 소수 (1~2)의 Entity만 존재하는게 대부분이다.

## Aggregate Root
- **Domain 로직을 주로 구현한다.**


- Aggregate의 일관성관리의 책임을 지는 객체


- Aggregate에 속한 객체는 직,간접적으로 AggregateRoot에 속한다.

- Aggregate에 속하는 객체의 수정은 AggregateRoot를 통해 이루어진다.
  - Value타입은 Immutable로 구현한다.
  - 단순히 값만 변경하는 Setter를 Public으로 만들지 않는다.
  

- Aggregate는 다른 Aggregate를 수정하지 않는다.
  - 로직에서 수정해야 할 일이 있다면 ApplicationLayer에서 실행 할 것.


- Aggregate간의 참조는 AggregateRoot의 식별자를 통해서 이루어져야한다.
  - 객체를 통한 참조는 결합도를 높이는 결과를 낳고, 변경을 어렵게만든다.
  - Domain 단위로 다른 DB를 사용할 수 있기 때문이다.
  - Aggregate내부 Entity끼리는 참조를 사용한다.

## Aggregate에서의 Transaction 범위
- 트랜잭션의 범위는 작은 것이 좋다. (Lock에 따른 동시성 이슈 --> 처리량을 떨어뜨림)
- 한 Transaction에서 하나의 Aggregate만 변경이 되어야한다.


## AggregateRoot 식별자를 통한 검색쿼리의 성능문제
같은 DBMS라면 Join을 통해서 쉽게 가져올 것을 검색 쿼리 (1) + 연관대상 (N) 개의 쿼리가 발생한다.

- 데이터 조회를 위한 별도의 Repository를 구성한다.
- 별도의 Repository에서 Theta Join (조건 조인)을 통해서 한번의 쿼리로 필요한 데이터를 모두 가져온다.

## Aggregate를 Factory로 사용하자

ex) 차단을 당한 가게는 상품을 등록할 수 없는 도메인로직
- 아래 코드의 문제점은 DomainLogic이 ApplicationLayer에 노출되어 있다는 것

```java
public class RegisterProductService{
    public ProductId registerNewProduct(NewProductRequest req){
        Store account = accountRepository.findStroeById(req.getStoirdId());
        
        if(account.isBlocked())
            throw new StoreBlockedException();
        ProductId id = productRepository.nextId();
        Product product = new Product(id,account.getId()...);
        productRepository.save(product);
  }
}
```
- 아래와 같이 Aggregate를 Factory로 다른 Aggregate생성 역할부여를 통해서 도메인 안에만 로직이 존재하게 할 수 있다.
```java
public class Store{
    public Product createProduct(ProductId newProductId...){
        if(isBlocked())
            throw new StroeBlockedException();
      return new Product(newProductId,...);
    }
}
```