# Optional
- NPE를 방지하기 위해 Java8에 추가된 기능
- 값이 있을 수도 있고 ,없을 수도 있다.

## 1. <T> Optional.of(T data)
- Optional로 Wrapping한 Data이다.
- null을 넣을 수 없다.

## 2. <T> Optional.ofNullable(T data)
- Data가 들어갈 수도 있고, null이 들어갈 수도 있다.

## 3. Optional.empty()
- null값이 들어가 있는 Optional 이다.

## 3. boolean isPresent()
- Optional 안의 값이, null이면 false, 실제 값이 들어가있으면 true를 리턴한다.

## 4. get()
- 안에 있는 값을 빼온다.
- 없으면 NoSuchElementException이 발생한다.

## 5. orElse(T other)
- null이라면 other를 받아온다.
- 값이 null이든 아니든 항상 불린다.
  - 값을 반환해야하기 떄문에, 갖고있어야 하기 때문이다.
  - Method를 OrElse에 넣어서 리턴값을 반환하려고 할 때에 버그를 유발 할 수 있다.

## 6. orElseGet(Supplier<? extends T> supplier)
- null이라면 supplier를 통해서 값을 가져온다.
- 해당 값이 null일 때만 불린다.

## 7. <X extends Throwable> T orElseThrow (Supplier <? extends T> exceptionSupplier) throws X
- null이라면 Exception을 던진다.

## 8. void ifPresent(Consumer <? super T> )
- null이라면 인자로 넘겨준 Consumer를 수행한다.

## 9. <U> map (Function <? super T, ? extends U> mapper)
- null이 아니라면 map을 통해서 값을 변경한다.

## 10. <U> flatMap (Function <? super T, ? extends Optional<? extends U>> mapper)
- null이 아니라면 flatMap을 통해서 중첩된 Optional의 값을 평탄화 + 변경한다.