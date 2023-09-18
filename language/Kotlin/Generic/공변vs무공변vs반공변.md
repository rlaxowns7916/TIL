# 무공변(불공변)
```kotlin
val goldFishCage = Caae<GoldFish>()
goldFishCage.put(GoldFish("금붕어"))

val fishCage = Cage<Fish>()
cage.moveFro(goldFishCage) // 무공변으로 인한 TypeMisMatch
cage.put(GoldFish("금붕어를 직접 옮기는건 되네?"))
```
- 상위 Type의 자리에는, 하위 Type이 들어갈 수 있다.
  - **Generic간의 상위Type - 하위 Type관계는 성립하지 않는다.**
  - Fish - GoldFish간에는 상위 하위 Type이 성립되지만, Cage<Fish> - Cage<FoldFish> 간에는 성립하지않는다.
- **(중요)배열은 무공변하지 않다. (=Java, Kotlin에서 배열은 공변하다)**
    - ```java
        class Example{
            // Java코드
            public static void main(String[] args){
                String[] strs = new String[]{"A","B","C"};
                Object[] objs = strs;
                objs[0] = 1; // RuntimeException
            }
        }
      ```
- Generic을 사용하는 Collection은 무공변하기 때문에, CompileTime에 Exception을 잡아낼 수 있다.

### 왜 무공변으로 설계했을까?
```kotlin
/**
 * 이게 된다고 가정해보자  (위 배열 예제와 동일한 원리)
 */

abstract class Animal
class Tiger : Animal
class Lion : Animal
class Cage<T>

fun main(){
    val tigerCage : Cage<Tiger> = Cage<Tiger>()
    val animalCage : Cage<Animal> = Cage<Animal>()
  
   animalCage = tigerCage
   animalCage.put(Lion())
  
  val tigers : List<Tiger> = animalCage.getAll() // Lion이 껴있다?
    
    
}
```

# 공변(Covariant)
- 상위 - 하위 Type의 관계(상속)이 Generic 까지 연장되는 것을 의미한다.
- 읽기전용이다
- 무공변 한 것은, Variance Annotation을 통해서 공변으로 변경 할 수 있다.
  - out (Kotlin)
  - extends (Java)

## 공변: 하위 Type -> 상위 Type (out)
```kotlin
class Cate<T>{
    val animals: MutableList<T> = mutableListOf()
    
    fun moveFrom(cage: Cage<out T>){
        cage.put() // Exception -> 공변 대상에, Data추가는 불가능하다. 
        this.animals.addAll(cage.animals) // 공변 대상에서 읽기만 가능하다.
    }
}
```
- 상위 Type Genric에, 하위 Type Generic을 넣는 것이다.
  - 위 방법으로 공변(변성)을 줄 수 있다.
- out을 통한 공변을 사용하면, 공변이 발생한 Type에 대한 데이터 생산(Insert)가 불가능하다.
  - 공변 대상은, 생산자 역할만 가능하다.
    - 소비자역할 (Insert)하는 역할을 제한한다.
  - 타입 안정성이 깨질 수 있기 때문이다.
- Class 레벨에도 적용이 가능하다.
  - 생성만 할 수 있고, 소비 (Consume)은 할 수 없다
  - **Return만 가능할 뿐, Parameter로 Generic을 소비하는 연산은 불가능하다.**
  - @UnsafeVariance 에노에이션을 통해서, out에서도 소비하는 것은 가능하다. (RuntimeException의 발생가능성을 높인다.)


# 반공변
- 공변의 반대이다.
- 쓰기전용이다.
- 하위 - 상위 Type의 관계가 Generic까지 연장되는 것을 의미한다.
- 하위 Type Generic에, 상위 Type을 집어 넣는 것이다.
- 무공변 한 것은, Variance Annotation을 통해서 공변으로 변경 할 수 있다.
  - in (Kotlin)
  - super (Java)


## 반공변: 상위 Type -> 하위 Type (in)
```kotlin
class Cate<T>{
    val animals: MutableList<T> = mutableListOf()
    
    fun moveFrom(cage: Cage<in T>){
        cage.animals.addAll(cage.animals) // 공변 대상에서 읽기만 가능하다.
    }
}
```
- 하위 Type Generic에, 상위 Type Generic을 넣는 것이다.
- in을 통해서, 무공변을 반공변으로 변경했을 떄, 대상 Type은 Consumer역할만 한다.