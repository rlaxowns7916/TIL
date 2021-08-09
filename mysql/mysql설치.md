<h1>MySQL 설치<h1>

```bash
sudo apt-get install mysql-server //mysql-server 설치
sudo ufw allow mysql //3306 포트 개방

sudo systemctl start mysql //mysql 스타트
sudo systemctl enable mysql // ec2 껏다 켜져도 mysql같이켜지게
```