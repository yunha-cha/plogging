# Spring Security 기본 세팅
매번 Spring Security를 구현하기 귀찮은 사람들을 위한 JWT 토큰 기반 기본 세팅입니다.

크게 사용 가능한 기능은 아래와 같습니다.

- 회원가입
- 구글 소셜 로그인
- 로그인
- JWT 토큰 발행
- JWT 토큰 검증
- refresh-token 발행, 검증, 새로운 토큰 발행
- 유저 로그인 시 마지막 로그인 시간 기록
- CORS 설정
- 웹소켓 통신
## 기본 세팅
먼저 ```main/resources/application.yml``` 파일을 적절히 수정해야합니다. 없다면 생성하세요.
<details>
<summary>application.yml 세팅 예시</summary>

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


### 요청 URL별 인증, 인가 여부 
com/website/security/config/SecurityConfig.java 설정에서 변경할 수 있습니다.  
자세한 사항은 주석을 참고하세요. 기본 설정은 아래와 같습니다.  
1. **모든 요청을 인가하는 url**  
```/api``` 로 시작하는 모든 요청


2. **JWT 토큰 검증이 필요한 url**  
```/auth``` 로 시작하는 모든 요청


3. **나머지 요청**  
모두 ```Denied```

### 회원가입 관련 로직
- **URL** : ```POST /api/user/signup```
- **설명** : 새로운 사용자를 등록합니다.
- **Content-Type** : ```multipart/form-data```


| 필드명        | 타입     | 필수 | 설명     |
| ---------- | ------ | -- | ------ |
| `userId`   | string | ✔️ | 사용자 ID |
| `password` | string | ✔️ | 비밀번호   |
| `nickname` | string | ✔️ | 닉네임    |

회원가입은 ```com/website/user/UserController.java```가 담당합니다.  
```userId, password, nickname```을 받아 회원가입을 진행합니다.  
각 필드가 비어있거나, ```userId``` 또는 ```nickname```이 중복되었다면 ```BadRequest(400)``` 을 반환합니다.  
회원가입 요청 테스트 JS는 아래와 같습니다.
```javascript
const signup = () => {
    const form = new FormData();
    form.append('userId', '아이디값');
    form.append('password', '비밀번호값');
    form.append('nickname', '닉네임값');
    axios.post('http://localhost:8080/api/user/signup', form)
}
```
회원가입에 성공했다면 201 코드를 응답받습니다.
### 로그인 관련 로직
- **URL** : ```POST /login```
- **설명** : JWT Access Token 및 Refresh Token 발급
- **Content-Type** : ```multipart/form-data```


| 필드명        | 타입     | 필수 | 설명              |
| ---------- | ------ | -- | --------------- |
| `username` | string | ✔️ | 사용자 ID (userId) |
| `password` | string | ✔️ | 비밀번호            |

로그인은 ```com/website/security/jwt/LoginFilter.java```가 담당합니다.  
로그인 요청 테스트 JS는 아래와 같습니다.
```javascript
const login = async () => {
    const form = new FormData();
    form.append('username', '아이디값');
    form.append('password', '비밀번호값');
    const res = await axios.post('http://localhost:8080/login', form);
    console.log('JWT Token :',res.headers["authorization"]);
}
```
로그인에 성공했다면 200 코드를 응답받습니다. 위 코드로 발급된 JWT 토큰을 확인할 수 있습니다.
### 요청 테스트
여러 요청 테스트용 Controller를 제공합니다. 아래 코드를 사용하여 정상적인 요청이 가는지 확인할 수 있습니다.
```javascript
const openUrlRequestTest = async () => {    //모두가 접근 가능한 HTTP요청 테스트
    const res = await axios.get('http://localhost:8080/api/test');
    console.log(res);
}
```
```javascript
const authUrlRequestTest = async () => {
    const res = await axios.get('http://localhost:8080/auth/test', {
        headers:{
            Authorization: '이 곳에 문자열로 /login 테스트를 진행하고 console에 출력된 JWT 토큰을 복사해서 넣으세요.'
        }
    });
    console.log(res);
}
```
테스트에 성공했다면 200코드를 응답받습니다.  
### JWT 정보
- 서명 방식 : ```HS256 (HMAC-SHA256)```
- Secret Key : ```application.yml```에 작성된 secret-key
- 토큰이 갖고 있는 정보
  1. ```userCode``` : 유저 기본 키
  2. ```userId``` : 유저 아이디
  3. ```role``` : 유저 권한

### 실제 테스트
간단히 테스트 해볼 수 있는 React Component의 전체 코드는 아래와 같습니다.  
직접 React코드를 수정해서 여러 요청을 해보세요.
<details>
<summary>React 테스트 코드</summary>

```javascript
import axios from 'axios'

function App() {
  const signupTest = () => {
    const f = new FormData();
    f.append('userId', 'test');
    f.append('nickname', 'testNickname');
    f.append('password', 'test')
    const res = axios.post('http://localhost:8080/api/user/signup', f);
  }
  const loginTest = async () => {
    const f = new FormData();
    f.append('username', 'test');
    f.append('password', 'test');
    const res = await axios.post('http://localhost:8080/login', f);
    console.log(res.headers["authorization"]);

  }
  const openUrlRequestTest = async () => {
    const res = await axios.get('http://localhost:8080/api/test');
    console.log(res);
  }
  const authUrlRequestTest = async () => {
    const res = await axios.get('http://localhost:8080/auth/test', {
      headers: {
        Authorization: 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJhY2NvdW50Q29kZSI6MSwiYWNjb3VudElkIjoidGVzdCIsImFjY291bnRSb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzUwMjg5OTQ0LCJleHAiOjE3NTAzMjU5NDR9.tEQwVIuT8jvKlf-Qgel-2v6tpHAdIBGXjNCt0qHaU08'
      }
    });
    console.log(res);
  }
  return (
    <div>
      <button onClick={signupTest}>회원가입</button>
      <button onClick={loginTest}>로그인</button>
      <button onClick={openUrlRequestTest}>open api 경로</button>
      <button onClick={authUrlRequestTest}>일반 경로</button>
      <a href="http://localhost:8080/oauth2/authorization/google">
        <button>Google로 로그인하기</button>
      </a>
    </div>
  )
}

export default App
```
</details>


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
