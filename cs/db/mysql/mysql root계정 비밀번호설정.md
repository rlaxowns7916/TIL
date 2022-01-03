<h1>Root 계정 패스워드 변경</h1>

```sql
[5.7.6 이후]
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '[비번]';

[5.7.6 이전]
SET PASSWORD FOR 'root'@'localhost' = PASSWORD('비번');

[변경사항 정장 (usr테이블 건드렸을 시)]
FLUSH PRIVILEGES
```