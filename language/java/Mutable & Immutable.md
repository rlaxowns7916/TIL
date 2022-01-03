# Mutable (가변객체)
- 값이 변경 가능한 것
- 멀티 쓰레드 환경에서 값을 공유 하려면 별도의 처리를 해주어야한다.
- append(),add()등으로 이어붙임

- List
- HashMap
- StringBuilder
- StringBuffer  
    . <br>
    . <br>
    .

# Immutable (불변 객체)
- 값이 변경 불가능 한 것
- 값이 변경 되는 것 아닌가? --> 새롭게 객체를 할당 받고 래퍼런스를 변경
- 객체에 대한 신뢰도의 상승 
- 동기화 처리없이 객체 공유 가능 (Thread-safe)


- String
- Boolean
- Integer
- Float
- Long
