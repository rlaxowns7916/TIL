# Topic
- Record를 구분하기 위한 논리적인 저장소이다.
    - DB의 테이블이나, FileSystem의 폴더와 유사하다.
    - 한 개 이상의 Partition으로 이루어져 있다.
- Producer가 공급하고, Consumer가 소비한다.
    - Producer와 Consumer는 서로를 알지 못한다.
    - Producer와  Consumer는 각자의 속도로 Topic을 통해서 Read와 Write를 수행한다.
- 내부적으로 하나 이상의 Partition으로 구성된다.
    - Producer가 보낸 Message는 Topic 내부의 Partition중 하나에 저장된다.

### Topic 내의 모든 Message 순서보장
- 복수개의 Partition이 존재하면 순서가 보장되지 않는다.
- 모든 Message순서를 보장하려면, **Partition 1개**만 사용해야 한다.

### Key를 이용한 순서보장
- 동일한 Key는 동일한 Partition에 전달이 된다.
- 순서가 필요한 작업은 하나의 파티션에서 계속해서 처리하게 하는 것이다
- 순서보장이 필요할 경우, 동일한 Key의 Message를 통해 순서를 보장 할 수 있다.
- **파티션의 개수가 바뀌게되면 Global Relocation이 발생하기 떄문에 SideEffect가 발생할 수 있다.**
