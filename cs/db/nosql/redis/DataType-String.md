# DataType - String

## String
- Redis가 제공하는 가장 기본적인 DataType이다.
- BinarySafe하다. (모든 데이터를 저장 할 수 있다.)
- Redis가 제공하는 다른 DataType (List, Hash, Set, SortedSet)에 데이터가 적합하지 않다면, String이 좋은 대안일 것이다.
- 직렬화/역직렬화를 통해서 사용하면 된다.
- 많은 데이터를 작은 단위의 String으로 쪼개서 저장 할 수 있다.
- 512MB가 최대이다.

## 주요 사용처
1. Plain String
2. Caching
3. Counter
4. 고정된 Config 값

## Value 증가/감소

### 증가
#### incr
- Number Type 일 때, Value를 1 증가 시킨다.
- ```shell
    $ incr [key]
  ```
#### incrby
- Number Type 일 떄, Value를 index만큼 증가 시킨다.
- ```shell
    $ incrby [key] [index]
  ```

  
### 감소
#### decr
- Number Type 일 때 Value를 1 감소 시킨다.
- ```shell
    $ decr [key]
  ```
  
#### decrby
- Number Type 일 때 Value를 index 만큼 감소 시킨다.
- ```shell
    $ decrby [key] [index]
  ```