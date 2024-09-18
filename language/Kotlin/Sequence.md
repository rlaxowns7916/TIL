# Sequence
- LazyLoading을 지원하는 Collection
  - 실제로 데이터가 필요할 때 연산을 수행한다.
  - 중간 연산결과를 저장할 필요가 없다.
  - 종결연산을 수행하기 전까지는 연산을 수행하지 않는다.
- 순차 데이터 처리
  - 메모리에 전부 로드 하지 않고, 순차적으로 로드하여 처리한다 (효울성)
- 무한 데이터 스트림 연산에 대한 효율성 제공
  - ex) 소수를 무한히 생성하는 연산, 피보나치 함수 ...
- Coroutine에서 사용하면 안된다. (대신 Flow사용)
  - Sequence의 최종연산은 suspend function이 아니기 때문에, Blocking을 유발 할 수 있다.


# SequenceBuilder
- Sequence를 만드는데 사용되는 Builder
  - 지연된 방식으로 데이터를 생성하는 것을 목적으로 한다.
- yield()외의 종결연산은 허용되지 않는다.
  - yield()를 통해서 값을 반환하고, 다음 호출시까지 Emit을 중단한다.
  - forEach(), toList()와 같은 종결연산은 모든 데이터를 처리하려 하기 떄문에, LazyEvaluation의 이점을 누릴 수 없다.

## yield
- Emit + LazyEvaluation을 위한 함수
- 중단된 지점에서 다시 실행된다.
```kotlin
fun main() {
    val sequence = sequence {
        println("generate 1")
        yield(1)
        println("generate 2")
        yield(2)
        println("generate 3")
        yield(3)
    }

    /**
     * generate 1
     * 1
     * generate 2
     * 2
     * generate 3
     * 3
     */
    sequence.forEach { println(it) }
}
```