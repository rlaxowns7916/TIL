# Grafana 설정
- grafana.ini의 설정값을 따라간다.

## DataSource란?

- 시계열 데이터가 저장된 위치
    - 일정 시간 간격으로 배치된 데이터를 의미한다.
- 연결된 DataSource로 부터 Metric을 획득한다.
- 다양한 DataSource가 존재한다.

<img width="1496" alt="dataSource" src="https://user-images.githubusercontent.com/57896918/185949447-110c8815-8331-46e9-ab45-2d47762fa0d4.png">


## Organization 이란?
- 대시보드, 데이터소스 및 설정을 독립적으로 관리하기 위함이다.
- User는 두 이상의 Organization에 참여 가능하다.
- 독립적인 DataSource를 가진다.

## Team이란?

- User 그룹
- User는 둘 이상의 Team에 참여 가능
- Team별 접근 권하을 통해 관리를 효율적으로 할 수 있다.

## User
- Admin
- Editor
- Viewer

### 권한 별 수행 기능
|                       | Admin  | Editor | Viewer |
|-----------------------|--------|--------|--------|
| DashBoard 확인          | o      | o      | o      |
| DashBoard 추가, 수정,삭제   | o      | o      |        |
| Folder 추가, 수정,삭제      | o      | o      |        |
| PlayList 확인           | o      | o      | o      |
| Explore 접근            | o      | o      |        |
| DataSource 추가, 수장, 삭제 | o      |        |        |
| User 추가, 수정           | o      |        |        |
| Team 추가, 수정           | o      |        |        |
| Organization 설졍 변경    | o      |        |        |
| Team 설졍 변경            | o      |        |        |
| Plugin 설정             | o      |        |        |


## Plugin
- 추가적인 써드파티 라이브러리들을 추가 할 수 있다.
- https://grafana.com/grafana/plugins/
