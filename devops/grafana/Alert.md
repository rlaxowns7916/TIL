# Alert
- Grafana에서 특정 조건에 따라 다양한 플랫폼에 Alert를 보낼 수 있다.


## Contact Points
- Alert을 보낼 플랫폼에 대한 설정이다.
- MessageTemplate을 통해 보낼 Message에 대한 기본적인 설정이 가능하다.


## Notification Policy
- label을 통해서, Alert에 대한 정책을 매칭시킨다.
- 어떤 ContactPoints를 사용할 것인지, 언제 Alert를 하지 않을 것인지 등의 설정이 가능하다.

## Message Template
- 기본적인 Template이 제공되긴하나, 불필요한 정보를 많이 가지게된다.
- Annotation, Label등을 포함하여 Custom하게 사용 가능하다.

