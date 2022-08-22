# Grafana란
- 오픈소스 데이터 시각화 도구
- 저장위치에 관계없이 데이터검색, 시각화 및 알람 기능 제공
- https://grafana.com/docs/grafana/latest/datasources/


## 특징
1. 데이터 통합
    - 데이터 저장이 아닌, 각각의 데이터에 접근을 하는 것
2. 데이터 접근성
    - 데이터 접근에 용이
3. 쉬운 대시보드 구성


## Grafana 설치
```shell
$ mkdir -p ./grafana/config ./grafana/data

$ touch ./grafana/config/grafana/ini

$ chmod -R 777 ./grafana/data

$ docker run -d --name=grafana -p 3000:3000 \ 
-v /home/ubuntu/grafana/config:/etc/grafana \
-v /home/ubuntu/grafana/data:/var/lib/grafana \
grafana/grafana
```
- 최초 설정 시,  Id: adamin // pw: admin으로 접속 가능하다.