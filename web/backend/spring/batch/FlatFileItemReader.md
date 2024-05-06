# FlatFileItemReader
- Flat파일 (csv, txt 등, 고정 위치로 정의 된 데이터 필드, 특수문자로 구별된 데이터 행)을 읽는 ItemReader
- **Resource와 LineMapper 2가지가 필요하다.**

## 구성요소

```java

import java.util.ResourceBundle; /**
 *  FlatFileItemReader
 */

// 파일 인코딩
String encoding = DEFAULT_CHARSET;

// File을 읽을 때 Skip할 상단 Line의 수
int linesToSkip = 0;

// 해당 Command가 있는 Line을 무시한다.
String[] commands;

// 읽어야 할 Resource
Resource resource;

// String을 Object로 변환한다.
LineMapper<T> lineMapper;

// Skip된 Line의 원래 내용을 전달받는 Interface (handleSkip이 2번이면 2번 호출된다)
LineCallbackHandler skippedLinesCallback;
```

# 선언

```java
import javax.sound.sampled.Line;

public FlatItemReader flatItemReader() {
  return new FlatItemReaderBuilder<T>()
          .name("flatItemReader")
          .resource(new ClassPathResource("sample-data.csv"))
          // 구분자를 이용해서 읽어들이는 설정
          .delimited().delimiter(",")
          // 고정길이를 이용해서 읽어들이는 설정
          .fixedLength()
          // 고정길이 범위를 읽어들이는 설정
          .addColumns(Range...)
  // LineTokenizer를 통해서 구분된 각 항목이 객체의 Field명과 매핑되도록 설정
        .names(new String[]{"name", "age", "address"})
          // Line과 매핑할 객체 설정
          .targetTypes(Class clazz)
          // 무시할 Line의 comment 기호 설정
          .addComment(String comment)
          // Parsing 예외 Skip 여부 설정
          .strict(Boolean strict)
          // 파일 인코딩 설정
          .encoding(String encoding)
          // Skip 할 상단 Line 수 지정
          .linesToSkip(int linesToSkip)
          // 상태를 저장할 것인지 설정
          .saveState(Boolean state)
          // LineMapper 설정
          .setLineMapper(LineMapper<T> mapper)
          // LineTokenizer 객체 설정
          .setLineTokenizer(LineTokenizer tokenizer)
          // FieldSetMapper 객체 설정
          .setFieldSetMapper(FieldSetMapper<T> mapper)

}
```

# 순서
1. FlatItemReader가 한줄 씩 읽는다.
2. 읽은 한개의 Line을 LineMapper에 전달한다.
3. LineMapper가 FieldSetMapper에게 Line을 넘겨주고 FieldSet을 요청한다.
4. FieldSetMapper가 LineTokenizer를 이용하여, String Array로 파싱한다.
5. FieldSetMapper가 String Array를 FieldSet으로 변환하고 리턴한다.
6. LineMapper가 FieldSet을 받아서 Object로 변환하고 리턴한다.

# LineMapper
- Line한줄을 읽어서, Object로 리턴한다.
- LineTokenizer와 FieldSetMapper를 사용한다.
  - FieldSet
    - Line을 Field로 구분해서 만든 배열 토큰을 전달하고, 접근 가능하다.
    - JdbcTemplate의 ResultSet과 유사하다.
  - LineTokenizer
    - 입력받은 Line을 FieldSet으로 변환해서 리턴한다. 
    - 파일마다 형식이 다르기 때문에, 문자열을 FieldSet으로 변환하는 과정을 거친다.
  - FieldSetMapper
    - JdbcTemplate의 RowMapper와 동일한 패턴이다.
    - FieldSet객체를 받아, 원하는 Object로 변환한다.

