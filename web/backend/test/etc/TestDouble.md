# TestDouble
- Test를 위해 사용하는 가짜객체
- StuntDouble (영화 등에서 위험한 장면을 대신하는 것)에서 유래되었다.

## Dummy
- 아무것도 하지 않는 깡통객체

## Fake
- 단순한 형태의 기능은 제공 가능하나, Production 환경에서는 사용하기 부족한 객체
- ex) MemoryDB

## Stub
- Test에서 요청한 것에 대해 미리 준비한 **결과만을** 제공하는 객체
- *상태변화에 초점을 맞추고 있다.**
```kotlin
interface Repository {
    fun findDataById(id: Int): Data
}

/**
  *  Stub 객체 구현
  *  고정된 응답을 리턴한다.
  *  행위 기록(호출에 대한 기록, 횟수)은 부재하다.
  */

class SimpleRepositoryStub : Repository {
    override fun findDataById(id: Int): Data {
        // 미리 정의된 단순한 응답 반환
        return Data("fixed data")
    }
}
```

## Spy
- Stub이면서, 호출된 내용을 기록하여 보여줄 수 있는 객체
- 일부는 실제 객체처럼 동작하지만, 일부는 Stubbing하면서 사용할 수 있다.

## Mock
- 행위에 대한 기대를 명시하고, 그에 따라 동작하도록 만든 객체
- 호출되는 Method, 전달되는 Parameter, 호출 횟수 등에 대한 기대를 명시적으로 정의한다.