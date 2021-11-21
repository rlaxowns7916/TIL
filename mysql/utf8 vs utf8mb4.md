# UTF8 vs UTFmb4

- utf8은 한 문자를 표현하는데 가변 3Byte
- utf8mb4는 4바이트 사용
- 유니코드 U+10000 이상의 문자는 UTF-8로 인코딩을 하려면 4바이트가 필요
  (이모티콘이 그 중 하나)
  
## my.cnf 설정
```shell
[client] 
default-character-set = utf8mb4 

[mysql] 
default-character-set = utf8mb4 

[mysqldump] 
default-character-set = utf8mb4 

[mysqld] 
character-set-server=utf8mb4 
collation-server=utf8mb4_unicode_ci 
skip-character-set-client-handshake
```
- skip-character-set-client-handshake: 
  클라이언트의 문자 설정을 무시하고, Character-set-server의 값으로 설정 
  
## 데이터베이스 CharacterSet 변경
```sql
ALTER DATABASE {database_name} CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
```

## 테이블 CharacterSet 변경
```sql 
ALTER TABLE {table_name} CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
- CONVERT TO : 해당 테이블 및 칼럼까지 모두 변경