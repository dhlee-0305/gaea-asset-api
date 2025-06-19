# MySql DB 설정 방법

1. DB 조회
```
SHOW DATABASES;
```

2. DB 생성
```
CREATE DATABASE ASSET_MANAGER;
```

3. 조회할 DB 설정
```
use ASSET_MANAGER;
```

4. USER 생성
```
CREATE USER 'gaea'@'localhost' IDENTIFIED BY 'gaea1234!';

SELECT * FROM mysql.user;
```

5. USER 권한 부여
```
grant all privileges on asset_manager.* to 'gaea'@'localhost';
```

6. 변경한 권한 즉시 반영
```
flush privileges;
```

7. USER 권한 조회
```
SHOW GRANTS FOR gaea@'localhost';
```

8. 테이블 생성(테스트용)
```
CREATE TABLE DEVICE_INFO(
DEVICE_NUMBER INT PRIMARY KEY AUTO_INCREMENT,
DEVICE_NAME VARCHAR(64) NOT NULL,
CONTENTS VARCHAR(64),
REG_USER VARCHAR(64),
REG_DATE DATETIME DEFAULT now(),
MOD_USER VARCHAR(64),
MOD_DATE DATETIME
);
```