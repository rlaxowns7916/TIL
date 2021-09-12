## Enum

```typescript
enum GenderType {
    MALE, FEMALE
}

interface Student {
    name: string,
    age: string,
    school?: string,
    gender: GenderType
}

const man: Student = {
    name: 'kim',
    age: 25,
    gender: GenderType.MALE
}
```

1. EnumType은 JavaScript컴파일시 코드에 영향을 미친다.
2. 이러한 EnumType은 Numeric Enum이다.
3. JavaScript로 변환시 순서에 따른 index로 구분이된다.

```typescript
enum GenderType {
    MALE = "male",
    FEMALE = "female"
}

interface Student {
    name: string,
    age: string,
    school?: string,
    gender: GenderType
}
```

4. 이러한 EnumType은 String Enum이다.
5. JavaScript로 변환시 옆에 같이선언된 String으로 구분이된다.

## 문자열 리터럴

```typescript
interface Student {
    name: string,
    age: string,
    school?: string,
    gender: 'male' | 'female' | 'netrual'
}
```
Enum과 동일한 결과를 얻을 수 있다.
IDE를 통해 자동완성의 도움도 받을 수 있다. 