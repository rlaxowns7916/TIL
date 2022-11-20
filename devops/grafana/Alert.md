# Alert
- Grafana에서 특정 조건에 따라 다양한 플랫폼에 Alert를 보낼 수 있다.

<img width="1061" alt="스크린샷 2022-11-20 오후 5 11 21" src="https://user-images.githubusercontent.com/57896918/202892471-9a890bac-dedf-4915-bf1b-4d20447628ab.png">

## Panel
- panel에서 Alert 발생 조건, Label, Annotation 등 설정 가능하다.
- Alert조건을 달성 했을 경우 해당 부분에 Annotation이 생기고, 그 시점의 데이터를 참조 가능하다.

<img width="896" alt="스크린샷 2022-11-20 오후 5 14 21" src="https://user-images.githubusercontent.com/57896918/202892546-10e81555-048d-44d1-b9a9-2ae19ede6840.png">


## Contact Points
- Alert을 보낼 플랫폼에 대한 설정이다.
- MessageTemplate을 통해 보낼 Message에 대한 기본적인 설정이 가능하다.

<img width="673" alt="스크린샷 2022-11-20 오후 5 13 31" src="https://user-images.githubusercontent.com/57896918/202892562-97cda75b-cf60-47a3-9022-2d47aa63d907.png">



## Notification Policy
- label을 통해서, Alert에 대한 정책을 매칭시킨다.
- 어떤 ContactPoints를 사용할 것인지, 언제 Alert를 하지 않을 것인지 등의 설정이 가능하다.

<img width="753" alt="스크린샷 2022-11-20 오후 5 14 53" src="https://user-images.githubusercontent.com/57896918/202892485-320d0bf9-1503-438d-bcce-cb0d2b27d0b5.png">



## Message Template
- 기본적인 Template이 제공되긴하나, 불필요한 정보를 많이 가지게된다.
- Annotation, Label등을 포함하여 Custom하게 사용 가능하다

![스크린샷 2022-11-20 오후 5 12 09](https://user-images.githubusercontent.com/57896918/202892550-291f76bd-42f0-474c-ab9c-a623835ca2ec.png)

### Slack Noti
<img width="287" alt="스크린샷 2022-11-20 오후 5 17 59" src="https://user-images.githubusercontent.com/57896918/202892567-35cd0eb6-faa1-483a-b538-496770b846f7.png">



