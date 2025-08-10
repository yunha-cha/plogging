# Spring 세팅
매번 Spring Security를 구현하기 귀찮은 사람들을 위한 JWT 토큰 기반 기본 세팅입니다.
- 유저 로그인 시 마지막 로그인 시간 기록
- CORS 설정

## 기본 세팅
먼저 ```main/resources/application.yml``` 파일을 적절히 수정해야합니다. 없다면 생성하세요.

```yaml
file:
  upload-dir: C:\uploads\profile-image  # 파일이 업로드 되는 경로입니다.
  download-url: http://localhost:8080/uploads/  # 파일을 찾으라고 요청할 URL입니다.
custom:
  setting:
    cors: http://localhost:5173 # CORS를 허가할 주소 입니다. 리액트는 :3000으로 바꾸세요.
server:
  port: 8080  # server port

# db config
spring:
  jwt:
    secret: asdfie # 이 곳에 50자 이상 영문자를 무작위로 입력하세요. JWT 토큰 생성 비밀 키 입니다. 
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver         # DB드라이버 입니다. MySQL 사용자는 적절히 수정하세요.
    url: jdbc:mariadb://localhost:3306/security_test # DB URL입니다. security_test라는 데이터베이스가 존재해야합니다. 
    username: 데이터베이스 유저네임 입력
    password: 데이터베이스 비밀번호 입력
  devtools:
    restart:
      enabled: false
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: 발급받은 client id
            client-secret: 발급받은 클라이언트 보안 비밀번호
            scope: openid,profile,email
  # jpa config
  jpa:
    hibernate:
      ddl-auto: create # 한 번 실행 후 update로 변경하는게 좋습니다.
    properties:
      hibernate:
        show_sql: true  # 서버가 SQL실행 시 콘솔에 출력 여부
        format_sql: true  # 출력할 SQL을 이쁘게 만들어주는지 여부

# Logging Level
logging:
  level:
    root: info  # 기본 로그 설정
    # org.springframework.security: TRACE # 주석을 풀어서 security 상세 로그 보기
```
</details>



## 알아야할 정보들
먼저 모든 Java script 예제 코드는 axios 라이브러리를 사용합니다. 없다면 설치하고 진행하세요.   
<div style="color:#ff6a6a; font-weight: bolder">만약 구글 소셜 로그인을 제외하고 싶다면 ```Default``` branch를 pull 하세요.</div>

### 유저 테이블 생성
```resources/application.yml``` 세팅에 의하여 아래의 테이블을 자동으로 생성합니다.  
테이블의 컬럼을 바꾸고 싶다면 ```com/website/entity/User.java```에서 변경할 수 있습니다.  
**컬럼을 변경하려면 JWT 토큰 설정 등 여러 설정을 변경할 수 있어야합니다.**
1. 테이블 명: **user**  
 컬럼

   | 컬럼명               | 타입              | 설명              |
   | ----------------- |-----------------|-----------------|
   | `user_code`       | BIGINT (PK, AI) | 유저 고유 키         |
   | `user_id`         | VARCHAR         | 로그인 ID (Unique) |
   | `nickname`        | VARCHAR         | 닉네임 (Unique)    |
   | `password`        | VARCHAR         | 암호화된 비밀번호       |
   | `introduce`       | VARCHAR         | 자기소개            |
   | `role`            | VARCHAR         | 사용자 권한          |
   | `profile_img`     | VARCHAR         | 프로필 이미지 경로      |
   | `create_at`       | DATETIME        | 가입일             |
   | `last_login_time` | DATETIME        | 마지막 로그인 시간      |
   | `enable`          | BOOLEAN         | 계정 활성 여부        |
   | `oauth_provider`  | VARCHAR         | 소셜 로그인 담당자      |
   | `oauthId`  | VARCHAR         | 소셜 로그인 유일 값     |
   | `email`  | VARCHAR         | 이메일             |
   | `name`  | VARCHAR         | 이름(실명)          |



### 의존성
- lombok
- mysqlDriver
- mariaDriver
- Spring data JPA
- Spring security
- Spring devtools
- WebSocket
- JWT 0.12.3
- oauth2-client

### 테스트
- Postman
- React
