# WrapperClass & String 의 동일성

## Wrapper Class

- Primitive Type을 객체로 표현해야 할 때 사용된다.
- Integer, Character, Double 등
- PrimitiveType -> WrapperClass (Boxing)
    - AutoBoxing 과정에서 내부적으로 valueOf가 동작한다.
    - ```java
      class Main{
        public static void main(String[] args){
            List<Integer> nums = new ArrayList<>();
            for(int i=0;i<10;i++){
               nums.add(Integer.valueOf(i));
           }
         }
      } 
    ```
- WrapperClass -> PrimitiveType (UnBoxing)


### Wrapper Class의 Cache
- 내부적으로 Cache를 갖는다.
- valueOf를 통해 새로운 객체의 생성이 아닌, Cache에 존재하는 같은 객체를 리턴해주게 된다.
- equals 또한 내부적으로 갖고있는 Primitive Value끼리의 비교로 동작한다.


## String
- String을 생성하는 방법은 두가지가 있다.
  - 리터럴을 통한 할당
  - new를 통한 객체의 생성 및 할당
- 같은 문자열을 할당하더라도, literal은 같은 객체이며 new를 통한 할당은 다른 객체가 할당된다.

### Java String Constant Pool
- literal로 생성된 String은 String Constant Pool에 위치하게 된다.
  - new를 통한 연산은 StringConstantPool이 아닌 다른 Heap영역에 저장된다.

### intern
- 기존에 동등한 (equals) 객체가 이미 StringPool에 존재한다면, 그 객체를 그대로 리턴한다.
- 없다면 호출된 객체를 StringConstantPool에 추가하고, 그 객체의 Refrence를 리턴한다.