# order by optimization
- order by에 사용되는 Column이 Index를 타는게 베스트이다.
- order by에 사용되는 Column이 Index를 타고있지 않으면, FileSort를 사용한다.
  - 데이터의 양이 적다면, Memory 단의 SortBuffer를 이용해 처리한다.
  - 데이터의 양이 많다면, Disk 단의 FileSort를 이용해 처리한다.
- Explain을 사용하여 FileSort를 확인할 수 있다.
  - Extra에 Using filesort가 있으면, FileSort를 사용하고 있다는 것을 의미한다.
  - Key에 Index가 있다면 Index를 타는 것이다.
  

## 튜닝방법

### [1] FileSort를 사용하고 있을 때
1. sort_buffer_size 튜닝
  - fileSort는 임시테이블에 기록하고, SortBuffer를 통해서 정렬을 수행한다.
  - 정렬할 데이터가 많다면 Disk를 이용하기 때문에 성능이 잘 나오지 않는다.
  - sort_buffer_size를 증가시켜, Disk이용을 최소화 시킬 수 있다.
2. Single-Pass -> Two-Pass로 수정
   - Single-Pass: SortBuffer에 데이터를 모두 넣는다.
   - Two-Pass: PK와 정렬하는 Column만 넣는다.
   - 일반적으로는 SinglePass가 유리하나, 데이터가 많다면 TwoPass가 유리하다.
3. 문자열 Column 튜닝
   - 값 전체를 사용해서 정렬하지 않고, max_sort_length 값 만큼 잘라서 정렬하게 변경