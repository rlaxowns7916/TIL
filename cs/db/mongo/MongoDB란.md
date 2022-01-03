# Mongo DB
1. **웹 어플리케이션과 인터넷 기반을 위해 설계된 데이터베이스 관리시스템**<br>
2. **Schema에 맞출 필요없이 JSON구조의 Document를 통해서 복잡한 정보를 쉽게 저장가능**

## 핵심기능
1. ### Document Data Model
    - 관계형 데이터베이스의 Join같은 연산을 필요로 하지 않는다.
    - 데이터 구조의 변화에 쉽게 대응 가능하다.
    
2. ### ad hoc Query (==동적 쿼리)

3. ### Index
    - B-Tree로 구현되어있다.
    - PrimaryKey에 Unique와 Index가 붙어있다.
    - Secondary Index(추가적인 Index) 지원<br>
      (HBase 등과 같은 다른 Key-Value NoSQL들은 허용하지않음)
    - 관계형 데이터베이스에서 볼 수 있는 모든 Index의 적용이 가능하다.<br>
      (Index관리 사항 통일 가능)
      
4. ### 복제
   - Replica Set(복제세트) 기능을 통해서 복제를 제공한다.
   - MongoDB 서버는 분리된 물리장비에 별도로 존재하고 이를 **Node**라고 부른다.
   - Primary Node와 여러개의 Secondary Node로 구성된다.
     (Praimry RW, Secondary R)
   - 자동 복구를 지원한다. Primary가 장애를 일으키면, Secondary중 하나가 Primary가 되며,
     복구된 Primary는 Secondary가 된다.
     
5. ### 확장
   - 샤딩을 통한 범위기반 파티션 메커니즘 자동 지원
   - 자동 장애조치 (Replica Set으로 이루어져 있어야함)
   - Scaling Out에 용이
    
## Tool
1. ### 명령어 셸
    - SQL이 아닌 자바스크립트에 기반한 명령어 사용
    ```json
       use my_database
       db.users.insert({name: 'kim'}
   
       db.users.find()
    ```
2. ### 데이터베이스 드라이버
    - 각 언어에 적합한 방식으로 도큐먼트 표시 (ORM이 아니더라도)
        - Ruby-RubyHash || Python-Dictionary || Java-Map
3. ### 커맨드라인 툴
    - mongodump, mongorestore: DB 백업과 복구를 위한 표준 유틸리티
        - mongodump - 핫백업 용이
        - mongorestore - 사용이 쉬움
    - mongoexport, mongoimport: JSON,CSV,TSV 타입의 데이터를 export, import
    - mongostart: MongoDB 시스템에 계속 Pooling하여 연산의 수, 할당 된 가상메모리의 양, 커넥션 수 와 같은 유용한 데이터 제공
    
    
