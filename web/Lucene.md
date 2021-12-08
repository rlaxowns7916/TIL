# ApacheLucene

**Java로 만들어진 오픈소스 검색 라이브러리**

## 처리순서
1. 인덱싱(Indexing)
    - 역 인덱스(Inverted Index)생성
2. 토큰 스캔(Token Scan)
    - Tokenizer를 사용해 토큰 분리
3. 토큰 분석(Token Analysis)
    - 조사나 StopWord같은 기본처리를 해서 원형 생성
4. 빈도세기(Frequency Counting)
    - 토큰(단어)별 빈도수를 HashTable에 저장
5. 정렬(Sorting)
   - 검색을 위해 정렬 사용 (Quick/Merge)
6. 인코딩(Encoding)
   - 저장 효율을 위해 인코딩 사용(differential encoding)


## 스코어링 방법
**스코어는 0 ~ 1 의 범위**
- TF/IDF(5.0 > version)
- BM25(version >= 5.0) /<TF/IDF 개선>

- **둘이 큰 차이는 없다.**
- **단어가 많이 나올수록, 문서의 개수가 작을수록 문서의 길이가 짧을 수록 점수가 높아짐**
- 엘라스틱서치에서도 스코어링 로직으로 사용

## TF/IDF
- 단어빈도(Term Frequency) / 역문서빈도 (Inverse Document Frequency)
- TF: 특정문서(d)에 해당 단어 (t)가 나오는 수 tf(d,t)
- DF: 특정 단어(t)가 등장한 문서의 수 df(t)
- IDF: DF에 반비례 하는 값 IDF = log(n/df(t)) \<n : 총 문서의 수>
- norm: 문서길이 가중치
### TF/IDF의 의미
- 특정한 문서에서 많이 나오는 단어는 중요
- 여러문서에서 많이나오는 단어는 중요도가 떨어진다.