# ServiceDiscovery
- Target 서버를 새로 생성하거나 종료할 때마다 Prometheus를 재기동하는 것은 비횽율적이다.
- Taget 서버를 가져오기 위해서 Service Discovery를 설정한다.
- https://prometheus.io/docs/prometheus/latest/configuration/configuration/


## file_sd_config
- 파일로 이루어진 static config
- 파일이 변경 될 시에, 감지를 하고 바로 설정이 적용된다.
- json, yml 형식으로 작성되어야 한다.
- prometheus.yml의 설정을 바꿔주어야 한다.

### prometheus.yml


### file_sd.yml

### ServiceDisvoery 확인

