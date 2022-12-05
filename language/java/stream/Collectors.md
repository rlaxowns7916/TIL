# Collector
- Stream의 중단연산인 .collect()에 파라미터로 들어가는 인터페이스


## Collectors.toList()
- Stream을 List로 반환한다.

## Collectors.toSet()
- Stream을 Set으로 변환한다.

## Collectors.mapping(Function<? super T>,Collector<? super U,A,R> downstream);
- map + collector 이다.
- 기존에 존재하던 값을 map으로 변형 한 후, Collector연산을 통해서 다양하게 수행 가능하다.
- ```java
    class Example{
        public static void main(String[] args){
          List<Integer> numbers = Stream.of(1,-2,3,-4,5)
                        .collect(Collectors.mapping(num -> Math.abs(num),Collectors.toList())); // 1,2,3,4,5
    }
  }
  ```
  
## Collectors.reducing
- reduce와 똑같이 사용 가능하다.
- ```java
    import java.util.stream.Collectors;class Example{
        public static void main(String[] args){
          List<Integer> numbers = Stream.of(1,-2,3,-4,5)
                        .collect(Collectors.reducing(0, (x,y) -> x+y));
    }
  }
  ```
  

## <T,K,U> Collectors.toMap (Function <? super T, ? extends K> keyMapper, Function <? super T, ? extends U> valueMapper)
- Stream안의 데이터를 map의 형태로 변환해주는 Collector
- keyMapper: 데이터를 map의 Key로 변환해주는 Function T -> K
- valueMapper: 데이터를 map의 Value로 변환해주는 Function T -> U
- ```java
    class Example{
        public static void main(String[] args){
           Map<Long,User> USER_CACHE = userRepository.findAll()
                .stream().collect(Collectors.toMap(User::getId,Function.identity()));
        }
    }
  ```
  

## <T,K> Collectors.groupingBy<Fuction<? super T, ? extends K> classifier)
- Stream안의 데이터에 classifier를 적용했을 때, 결과 값이 같은 것끼리 List로 모아서 Map으로 반환해주는 collector
- Key는 Classifier의 결과값이며, value는 List형태의 데이터 들이다
  - ```java
    class Example{
        public static void main(String[] args){
           List<Integer> numbers = List.of(1,2,2,3,4,4,5,6);
           Map<Integer,List<Integer>> oddEvenClassifier = 
                numbers.stream().collect(
                    Collectors.groupingBy(
                        num -> num % 2
                    )       
                );
        } // {0 = [2,2,4,4,6], 1 = [1,3,5]}
    }
    ```
- 두번 째 매개변수로 Collector를 넘기는 것도 가능하다.
  - 기본적인 List의 Value가 아닌, Collector를 적용시킨 Map의 Value가 만들어진다.
  - ```java
        class Example{
        public static void main(String[] args){
           List<Integer> numbers = List.of(1,2,3,4,5,6);
           Map<Integer,Set<Integer>> oddEvenClassifier = 
                numbers.stream().collect(
                    Collectors.groupingBy(
                        num -> num % 2
                    ),
                    Collectors.toSet()
                );
           // {0 = [2,4,6], 1 = [1,3,5]}
        } 
    }
    ```
    
## Collector<T,?, Map<Boolean,D>> partitioningBy(Predicate<? super T> predicate)
- predicate를 받아서 true,false 두 key가 존재하는 Map을 만들어낸다.
-  ```java
    class Example{
      public static void main(String[] args){
          List<Integer> numbers = List.of(1,2,3,4,5);
          Map<Boolean,Integer> isEven = numbers.stream()
               .collect(Collectors.partitioningBy(it -> it % 2  == 0));  
      }
   }
    ```
- 두 번째 인자로 Collector를 넘기는 것도 가능하다.

