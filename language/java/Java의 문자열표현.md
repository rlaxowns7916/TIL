# Java의 문자열 표현
- Java의 Char은 2Byte를 가지며, 내부적으로 UTF-16을 사용한다.
- 대부분의 defaultCharset인 UTF-8은 1Byte~4Byte의 가변길이 인코딩을 가진다.
- 2Byte로 표현이 불가능한 문자를 어떻게 Java는 Char에 위치시킬까?
  - Unicode의 CodePoint: 0x0000 ~ 0x10FFFF
  - Java의 CodePoint: 0x0000 ~ 0xFFFF (Unicode를 표현할 경우 누락되는 경우가 생김)

## Surrogate Pair란?
- UTF-16처리를 위한 개념
  - 2Byte로 처리 할 수 있는 개념은 2Byte로 표현한다.
  - 2Byte로 처리 할 수 없는 개념은 4Byte로 처리한다. (Surrogate Pair 사용 -> 상위영역)
- Java가 표현할 수 없는 부분을 표현하기 위한 방식이다.
    - BMP (Basic Multilingual Plane): 단일 Char로 표현가능한 영역
    - 상위영역 (Supplementary Plane): 단일 Char로 표현이 불가능한 영역이며 Surrogate Pair로 표현한다.
- 2개의 Char로 구성된다.
    - High Surrogate: 0xD800 ~ 0xDBFF (상위 Bit 저장)
    - Low Surrogate: 0xDC00 ~ 0xDFFF (하위 Bit 저장)
- 동작 방식
  1. 코드포인트를 0x10000만큼 감소시킨다. (상위 영역은 0x10000 부터 시작하는 상대위치로 표현)
  2. 상위 10Bit를 High Surrogate에 저장시키고, 하위 10Bit는 Low Surrogate에 저장한다.

## 주의할 점
- InputStream에서 한개의 Byte를 받을 떄, Char로 변환할 경우, Surrogate Pair를 고려해야 한다.
- Character.toChars(it)를 통해서 Surrogate Pair를 고려한 Char 배열을 받는 것이 좋다.
- Surrogate Pair를 고려하지 않으려면, 모두 읽어 (readAllBytes) String에 Encoding을 넘기는 것이 좋다.

### Java에서 Char이 하나의 문자와 대응되지 않는 이유
```text
😊 (U+1F600) => Surrogate Pair
High Surrogate: 0xD83D
Low Surrogate: 0xDE00


UTF-8로 인코딩된 이모지 😊 (U+1F600):

[UTF-8 데이터 예시]
UTF-8 바이트: 0xF0 0x9F 0x98 0x80

[UTF-16 데이터 예시]
- High Surrogate: 0xD83D
- Low Surrogate: 0xDE00

InputStream.read()로 처리하면:

첫 번째 호출: 0xF0
두 번째 호출: 0x9F
세 번째 호출: 0x98
네 번째 호출: 0x80

- 2개씩 짤라써도, UTF-16이 표현되지 않는다.

```

## 예시
```kotlin
fun convert(ios: InputStream): String{
    val stringBuilder = StringBuilder()
    ios.use { stream ->
        generateSequence { stream.read().takeIf { it != -1 } }
            .map { Character.toChars(it) }
            .forEach { chars -> stringBuilder.append(chars) }
    }
    
    return stringBuilder.toString()
}
```