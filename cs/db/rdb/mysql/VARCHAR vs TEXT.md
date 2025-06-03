# VARCHAR vs TEXT
- 공통점
    - 문자열 속성을 저장한다.
    - 최대 65535 Byte까지 저장이 가능하다.
- 차이점
  - VARCHAR타입 컬럼에는 지정된 글자수만큼 데이터 저장 가능
  - TEXT타입은 인덱스 생성 시, 반드시 Prefix 길이 지정 필요
    - ```sql 
         create index idx_text_coumn on table (text_column(100)); 
      ```
  - TEXT 타입 컬럼은 표현식으로만 디폴트 값 지정 가능
    - ```sql
        create table tb1 (col TEXT DEFAULT 'abc') -- 에러 발생
        create table tb1 (col TEXT DEFAULT ('abc')) -- 정상
      ```
  - **VARCHAR는 RowSize제한(65535)에 거리지만, TEXT는 RowSize제한에 걸리지 않는다.**
    - TEXT 타입은 Off-Page 형태(pointer)로 저장되기 때문에, RowSize 제한에 영향을 받지 않는다.

## MySQL 세션에서 데이터를 읽는 방법
- 메모리에 버퍼공간(Memory Buffer)을 미리 할당하고 재활용
  - MemoryBuffer는 **같은 Row에 대해서 최대 Record 크기**를 기반으로 Memory를 미리할당한다. (실제 Row값이 작더라도 일단 최대치 할당)
  - ex) VARCHAR(5000) 타입의 컬럼이 있다면, MemoryBuffer는 5000Byte를 할당한다.
- MemoryBuffer에는 Disk에서 읽은 Row를 임시저장하거나, Sort/Join 등 임시결과를 처리할 때 사용한다.

### 그렇다면 VARCHAR과 TEXT는?
- VARCHAR 타입은 메모리 버퍼공간을 미리 할당해두며 재활용 가능하다.
- **TEXT 타입은 메모리 버퍼공간을 할당하지 않으며 Pointer(16~20Byte) 값만 가지고, 필요할 때마다 할당/해제**
  - 짧은 TEXT라면 In-Line 될 수도 있으나 **보통 Off-Page 형태로 저장** 하기 떄문에, 별도의 I/O가 발생 할 수 있다.
- 컬럼 사용이 빈번하고, 메모리 용량이 충분하다면 VARCHAR 타입 추천
- VARCHAR(5000)과 같이 길이가 긴 컬럼들을 자주 추가하는 경우, RowSize 제한에 걸릴 수 있으므로, TEXT 타입과 같이 사용하는 것을 추천


## VARCHAR의 길이는?
- 실제 최대 사용하는 길이만큼 명시해야 메모리 사용 효율 증가
- 디스크 공간효율도 미미하게 존재 (1Byte vs 2Byte)
  - 2^8(0~255)까지는 1Byte이기 때문

## 결론
- 상대적으로 저장되는 데이터 사이즈가 많이 크지않고, 컬럼 사용이 빈번하며, DB서버 메모리의 용량이 충분하다면 VARCHAR 타입을 사용하는 것이 좋다. (Buffer 공간 재활용)
- 저장되는 데이이터 사이즈가 큰편이고, 컬럼을 자주사용하지 않으며, 테이블에 다른 문자열 컬럼들이 많이사용되고 있는경우 TEXT 타입을 사용하는 것이 좋다. (RowSize 제한 회피)
- VARCHAR타입을 사용하는 경우, DB 리소스 최적화를 위해서 실제 사용되는 만큼만 지정하는 것이 좋다.
- Query의 Select 절에는 가능하면 필요한 컬럼들만 명시하는 것이 좋음
  - 테이블에 대형데이터 저장 컬럼 존재시 쿼리 처리 성능이 낮아질 수 있음