# Function Composition

- 합성 함수를 만드는 것이다.
- EX) f(x), g(x)의 합성 --> f(g(x))

## <V> Function<<V,R> compose (Function <? super V, ? extends T> before);

- before를 먼저 실행 한 후 자기 자신을 실행한다.

```java
class Example {

  public static void main(String[] args) {
    Function<Integer, Integer> multiple2 = x -> x * 2;
    Function<Integer, Integer> add10 = x -> x + 10;
    
    Function<Integer,Integer> composedFunction = add10.compose(multiple2);
    composedFunction.apply(10);
    
    //10 -> 10*2 -> 10*2 +10 
  }
}
```

## <V> Function <T,V> andThen (Function <? super R, ? extends V> after);
```java
class Example {

  public static void main(String[] args) {
      Order order = new Order();
      
      Function<Order,Order> mergedProcessor =
          getProcessors().stream()
              .reduce(Function.identity(),(p1,p2) -> p1.andThen(p2));
      
      mergedProcessor.apply();
  }
  private static List<Function> getProcessors(){
    return List.of(
        new PriceProcessor(),
        new TaxProcessor(9.3)
    );
  }  
}

class PricesProcessor implements Function<Order,Order>{
  
  @Override
  public Order apply(Order order){
    return order.setPrice(
      order.getOrderLines().stream()
          .map(OrderLine::getPrice)
          .reduce(0,(x,y) -> x+y) 
    );
  }
}

class TaxProcessor implements Function<Order,Order>{
  
  private double taxRate;
  
  public TaxRate(double taxRate){
    this.taxRate = taxRate;
  }
  
  @Override
  public Order apply(Order order){
    order.setTax(
        order.getPrice() * (taxRate / 100)
    );
  }
}
```
- 자기 자신을 실행 한 후 after를 실행한다.