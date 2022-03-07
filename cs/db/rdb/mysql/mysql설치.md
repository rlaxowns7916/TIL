## MySQL 설치

```bash
sudo apt-get install mysql-server //mysql-server 설치
sudo ufw allow mysql //3306 포트 개방

sudo systemctl start mysql //mysql 스타트
sudo systemctl enable mysql // ec2 껏다 켜져도 mysql같이켜지게
```

## JDBC 설치
```shell
 #jdbc 드라이버
 apt install libmysql-java
 
 #톰캣에 옮기기
 ln -s /usr/share/java/mysql-connector-java.jar /usr/share/tomcat8/lib/connector-java.java
```