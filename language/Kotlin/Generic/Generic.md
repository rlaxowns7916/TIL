# Generic

## 타입 Casting 에서 가장 흔하게 발생하는 문제
```kotlin
class Cage{
    private val animals: MutableList<Animal> = mutableListOf()
    
    fun getFirst() : Animal{
        return animals.first()
    }
    
    fun put(animal: Animal){
        this.animals.add(animal)
    }
}

abstract class Animal(val name:String)
abstract class Fish(name:Stribng) : Animal(name)

class GoldFish(name:String) : Fish(name)


fun main(){
    val cage: Cage<Animal> = Cage()
    cage.put(GoldFish("금붕어")) // GoldFish타입을 Cage에 Put
    
    val goldFish: GoldFish = cage.getFirst() // Error: Animal타입으로만 받을 수 있음
}
```

### 해결법 1: Casting
```kotlin
fun main() {
    val goldFish = cage.getFist() as GoldFish
}
```
- CompileTime에 Error를 잡을 수 없다.
  - Animal을 확장한 어떠한 Class도 들어갈 수 있기 때문이다.
- Runtime에 Error를 발견할 수 있다.

### 해결법 2: Safe TypeCasting & ElvisOperator
```kotlin

val goldFish : GoldFish = cage.getFirst() as? GoldFish 
        ?: throw RuntimeException("Type Casting Failure")

```

### 해결법 3: Generic
```kotlin
class Cage<T>{
    private val animals: MutableList<T> = mutableListOf()

  fun getFirst() : T{
    return animals.first()
  }

  fun put(animal: T){
    this.animals.add(animal)
  }
}

fun main(){
    private val cage = Cage<GoldFish>()

    val cage = Cage()
    cage.put(GoldFish("금붕어")) // GoldFish타입을 Cage에 Put
    
    val goldFish: GoldFish = cage.getFirst() // Error: Animal타입으로만 받을 수 있음
}
```
- Generic 적용을 통해 CompileTime에서 Type-Safe한 Operation을 수행할 수 있다.

## Type 상한
```kotlin
class Cage <T : Animal>{
    private val animals = mutableListOf<T>()
}
```
- 타입 파라미터의 상한을 정의할 수 있다.
- 이 타입을 포함한 하위타입만 들어올 수 있어진다.
- 예를들어서, non-null Type을 강제하고 싶다면, <T : Any>와 같은 방법도 있다.

### where
```kotlin
class Cage<T>(
  private val animals:MutableList<T> = mutableListOf()
) where T : Animal, T : Comparable<T>{
    fun printAfterSorting(){
        this.animals.sorted()
                .map { it.name }
                .let { ::println }
    }
}
```
- where을 통해서 Kotlin의 TypeParameter에 여러가지 제약을 걸 수 있다.
- 위의 예제는, Animal타입이면서 Comparable이 구현된 타입을 상한으로 걸고있다.
  - Comparable을 가지고있기 떄문에 정렬기능을 사용할 수 있어진다.
- Kotlin에서 제공하는 명시적인 타입하한 문법은 없다.

## Generic Class의 상속
- Generic을 사용하는 Class의 상속도 가능하다.
```kotlin
open class CageV1<T : Animal>
/**
 * 방법 1
 * 제약조건이 같아야 한다.
 */
class Cage2 <T : Animal> : CageV1<T>()

/**
 * 방법 2
 * 애초에 T를 정의해서 넘긴다.
 */

class Cage3() : CageV1<Animal>()
```

## TypeAlias
- Generic을 사용하면 네이밍이 길어지는 것을 방지하기 위한 것이다.
```kotlin
typealias PersonDtoStore = Map<PersonDtoKey, MutableList<PersonDto>>

fun handleCacheStore(store: PersonDtoStore){
    
}
```