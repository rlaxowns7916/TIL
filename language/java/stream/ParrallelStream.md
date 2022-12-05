# ParallelStream
- 기존의 Stream은 Sequential
- Stream 연산을 병렬적으로 처리하는 것이다.
  - 종결연산에 따른 순서가 일정하지 않을 수 있다.
- 속도가 항상 빨라지는 것은 아니다.
  - 공통으로 사용하는 리소르가 있을 경우 정합성 오류가 발생 할 수 있다.
    - 이러한 문제를 해결하기위해서 mutex, semaphore를 사용하면 더 느려질 수 있다.
  - 간단한 연산에서는 오히려 더 느려질 수 있다.
- 순서에 상관없는 것에 사용하기 좋다.
- 별도의 설정이 없다면, 생성된 쓰레드 중 메인쓰레드를 제외한 것들이 할당된다.
- 종결처리를 주의해야 한다.
  - ex) limit, findFirst와 같은 연산은 사용하면 안된다.
- 성능 최적화용도로 사용 가능하다.

## parallel
```java
class Example{

  public static void main(String[] args) {
    List<Integer> numbers = List.of(1,2,3,4,5,6,7,8);
    numbers.stream().parrallel()
        .filter(it -> it % 2 == 0)
        .forEach(it -> doSomething(it));
      // stream().parallel() 과 parallelStream()과의 차이는 없다.
  }
  public static void doSomething(int num){
    ...
  }
}
```