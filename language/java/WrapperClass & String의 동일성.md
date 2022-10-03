# WrapperClass & String 의 동일성

## Wrapper Class

![WrapperClass 구조도](https://user-images.githubusercontent.com/57896918/193581238-f38deef8-af47-451f-9f4f-b2ec19c8bec4.png)

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

#### Cache
<img width="576" alt="char Cache" src="https://user-images.githubusercontent.com/57896918/193581151-63cfa9f7-c137-4f24-bf75-4c82a2fd9723.png">

#### valueOf

<img width="451" alt="valueOf" src="https://user-images.githubusercontent.com/57896918/193581205-5659fa71-0e43-4681-8e2c-3c4127b76617.png">

#### equals
<img width="455" alt="equals" src="https://user-images.githubusercontent.com/57896918/193581316-88c50f2d-b1be-4798-8738-63c10dc5b49c.png">


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

![스크린샷 2022-10-03 오후 9 50 56(2)](https://user-images.githubusercontent.com/57896918/193581413-c45a33f7-459f-4437-88b8-4e2624754dce.png)

