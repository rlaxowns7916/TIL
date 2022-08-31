# ServiceDiscovery
- Target 서버를 새로 생성하거나 종료할 때마다 Prometheus를 재기동하는 것은 비횽율적이다.
- Taget 서버를 가져오기 위해서 Service Discovery를 설정한다.
- https://prometheus.io/docs/prometheus/latest/configuration/configuration/
- 다양한 ServiceDiscovery 형식을 지워한다.


## file_sd_config
- 파일로 이루어진 static config
- 파일이 변경 될 시에, 감지를 하고 바로 설정이 적용된다.
- json, yml 형식으로 작성되어야 한다.
- prometheus.yml의 설정을 바꿔주어야 한다.

### prometheus.yml

<img width="330" alt="스크린샷 2022-08-17 오후 11 21 02" src="https://user-images.githubusercontent.com/57896918/185158581-6aba9a6b-6234-4201-a85f-91a5f1dfad40.png">


### file_sd.yml
<img width="319" alt="스크린샷 2022-08-17 오후 11 19 44" src="https://user-images.githubusercontent.com/57896918/185158263-1d7cc2bc-6630-4b1b-98a6-960c312d148a.png">

- label 속성으로 추가 MetaData를 줄 수 있다.
- PromQL을 사용하여 검색 하는 것도 가능하다.

### ServiceDisvoery 확인

![Servife Discovery In Web](https://user-images.githubusercontent.com/57896918/185158131-0aa47614-e5f6-4ec0-b32a-c7221f530e17.png)
