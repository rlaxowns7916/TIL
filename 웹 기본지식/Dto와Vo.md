# Dto & Vo

## Dto (DataTransferObject)
- Mutable한 객체이다.
- Layer간 데이터 교환을 목적으로 하는 객체
- 데이터 교환에 목적을 두기 떄문에 로직을 두지 않는다.
  - 정렬, 직렬화 등에 대한 로직은 가질 수 있음 
- Getter/Setter만을 갖는다.


## Vo (ValueObject)
- Immutable한 객체이다.
- 로직을 가질 수 있다.
- 동등성을 보장한다.