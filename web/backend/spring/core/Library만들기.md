# Spring Library 만들기

# [1] META-INF
- manifest를 담는 파일
- Jar파일과 관련된 Metadata 저장
  - 스펙
  - 사용 메뉴얼

# [2] spring.factories
- META-INF 폴더 하위에 위치한다.
- SpringFramework 특정 구성요소 AutoConfiguration 하는데 사용되는 설정파일
```text
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.mylibrary.MyAutoConfiguration
```

## (Optional) org.springframework.boot.autoconfigure.AutoConfiguration.imports
- META-INF/spring 하위에 위치한다.
- spring.factories의 역할을 나눠가진다.
  - EnableAutoConfiguration을 대체한다.

# [3] Configuration
```kotlin
@Configuration
@ConditionalOnPropetry(
  prefix = "mylibrary",
  name = "feature.enabled", // mylibrary.feature.enabled 프로퍼티가 존재하는지 확인
  havingValue = "true", // havingValue가 true일 때만 Bean을 등록
  matchIfMissing = true  // 프로퍼티가 존재하지 않더라도, 조건을 참으로 간주하며 Bean을 등록
)
class MyAutoConfiguration{
  @Bean
  @ConditionalOnBean(Sample::Class) // Sample Bean이 존재할 때만 Bean을 등록
  fun myBean(): MyBean{
    return MyBean()
  }
}


```