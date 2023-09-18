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
    val cage = Cage()
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