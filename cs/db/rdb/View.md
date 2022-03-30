# View
- 하나의 Select문이라고 볼 수 있다.
- 가상의 논리적인 테이블이다.
  - 물리적인 공간이 할당되지 않는다.
  - Data Dictionary Table에 View에 대한 정의만을 한다.
  - Data가 저장되지 않고, SQL문만 저장되는 것이다.
- View를 통해서 Insert,Select,Update,Delete가 가능하다.
  - **주로 Select를 위해서 사용된다.**
- View를 이용하여 View를 정의 할 수 있다.
- ALTER 명령어가 없다.
  - 수정하려면 DELETE 후 CREATE 해야 한다.

## View의 장점
- 보안에 유리하다.
  - 테이블에 접근할 수 없는 권한을 가진 사용자에게 뷰의 권한은 허용함으로 보안을 지킬 수 있다.
- 사용자 편의성을 높일 수 있다.
- SQL이 간소화된다.

## View 생성 문법
```sql
CREATE VIEW  뷰이름 AS SELECT ~ ;
```