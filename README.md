## 프로젝트 구성

- Spring Boot 2
- Java 8
- H2
- JPA 
- QueryDsl
- Gradle 5
- Mockito
- Lombok





## 빌드 및 실행 방법

1. **프로젝트 clone**

   ```shell
   git clone https://github.com/expitly/housing-finance.git
   ```

2. **프로젝트로 이동**

   ```shell
   cd housing-finance
   ```

3. **빌드**

   ```shell
   ./gradlew build
   ```

   * <u>linux 계열에서 실행시 gradlew 파일에 권한이 없으면 권한을 설정 해줘야합니다.</u>

4. **실행**

   ```shell
   ./gradlew bootRun
   ```


* **H2 Console 접속**

  > http://localhost:8080/h2-console





## 문제 해결 전략

1. **인증/권한 관리**
   - Password 저장시 BCrypt 사용
   - JWT 서명 Secret, 토큰 유효기간은 properties로 분리하여 저장
     - 서명 알고리즘은 HS256을 사용
     - 토큰 유효기간은 2시간으로 설정, properties를 통해 변경 가능
   - Spring Security를 활용하여 유저/권한 관리
     - 권한은 여러 권한이 필요 없기 때문에 "*ROLE_API*" 하나로 사용하고, 여러 권한을 추가 할 수 있도록 테이블로 관리
   - Token에 Role에 대한 정보를 담아 Resource 접근시 DB에 조회하지 않고 유저의 권한 제어
2. **테스트**
   - Unit Test 개발시 Mockito를 적절히 활용하여 개발
3. **API 개발**
   - 요구사항 개발시, JPA, QueryDSL, JAVA8의 stream을 적절히 활용하여 데이터 추출
   - Entity 설계
     - Bank(id, name, code)
     - Supply(id, year, month, amount)
   - Option(예측 알고리즘) 문제의 경우, 과제 기간 내 학습 및 구현에 무리가 있을 것으로 판단하여 기본 기능에 충실하기로 판단
4. **기타**
   - 권한("*ROLE_API*")과 은행 정보 CRUD에 대한 별도 API 명세는 없으므로 기존에 존재한다고 가정하고 data.sql 파일을 통해 Application 초기 구동시 DB에 데이터 Insert 처리





## API 명세

### 1. 계정 생성

입력으로 ID, PW 받아 내부 DB에 계정 저장하고 토큰을 생성하여 출력한다.

**URL** : `/api/v1/auth/signup`

**Method** : `POST`

**Parameters** : 

- username: 이름
- password: 비밀번호

**Reqeust Example**

```shell
curl -X POST \
  http://localhost:8080/api/v1/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{
	"username": "user",
    "password": "1234"
}'
```

**Response Example**

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0MzY0NywiZXhwIjoxNTU0NjUwODQ3fQ.HhzSNjziE53bFVn3vXbFsgPU4FzMcG8IwrYhOmxKRUA"
}
```



### 2. 로그인

입력으로 생성된 계정 (ID, PW)으로 로그인 요청하면 토큰을 발급한다. 

**URL** : `/api/v1/auth/signin`

**Method** : `POST`

**Parameters** : 

- username: 이름
- password: 비밀번호

**Reqeust Example**

```shell
curl -X POST \
  http://localhost:8080/api/v1/auth/signin \
  -H 'Content-Type: application/json' \
  -d '{
	"username": "user",
    "password": "1234"
}'
```

**Response Example**

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc0MCwiZXhwIjoxNTU0NjUwOTQwfQ.-L3Fk5wzBpMI96QuPEDPHmFQlC26O3xU5NRE16w2FqE"
}
```



### 3. 토큰 재발급

기존에 발급받은 토큰을 Authorization 헤더에 “Bearer Token”으로 입력 요청을 하면 토큰을 재발급한다.

**URL** : `/api/v1/auth/refresh`

**Header**: `Authorization: Bearer TOKEN`

**Method** : `POST`

**Reqeust Example**

```shell
curl -X POST \
  http://localhost:8080/api/v1/auth/refresh \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc0MCwiZXhwIjoxNTU0NjUwOTQwfQ.-L3Fk5wzBpMI96QuPEDPHmFQlC26O3xU5NRE16w2FqE'
```

**Response Example**

```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc5NywiZXhwIjoxNTU0NjUwOTk3fQ._Z0ccRYjehEJNgkQP9-l0xJu2wj5IhNcix3PWvmIOWI"
}
```



### 4. 공급 현황 파일 데이터 저장

데이터 파일을 입력받아 각 레코드를 데이터베이스에 저장한다.

**URL** : `/api/v1/supplies/csv`

**Header**: `Authorization: Bearer TOKEN`

**Method** : `POST`

**Reqeust Example**

```shell
curl -X POST \
  http://localhost:8080/api/v1/supplies/csv \
  -H 'Content-Type: multipart/form-data' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc5NywiZXhwIjoxNTU0NjUwOTk3fQ._Z0ccRYjehEJNgkQP9-l0xJu2wj5IhNcix3PWvmIOWI' \
  -F "file=@/c/Users/JinYoung/Desktop/data.csv"
```



### 5. 주택금융 공급 금융기관(은행) 목록 조회

주택금융 공급 금융기관(은행) 리스트를 출력한다.

**URL** : `/api/v1/banks`

**Header**: `Authorization: Bearer TOKEN`

**Method** : `GET`

**Reqeust Example**

```shell
curl -X GET \
  http://localhost:8080/api/v1/banks \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc5NywiZXhwIjoxNTU0NjUwOTk3fQ._Z0ccRYjehEJNgkQP9-l0xJu2wj5IhNcix3PWvmIOWI'
```

**Response Example**

```json
[
    {
        "name": "주택도시기금",
        "code": "MOLIT"
    },
    
    ...
    
    {
        "name": "외환은행",
        "code": "KEB"
    },
    {
        "name": "기타은행",
        "code": "ETC"
    }
]
```



### 6. 연도별 각 금융기관의 지원금액 합계 조회

연도별 각 금융기관의 지원금액 합계 리스트를 출력한다.

**URL** : `/api/v1/supplies/groups/year`

**Header**: `Authorization: Bearer TOKEN`

**Method** : `GET`

**Reqeust Example**

```shell
curl -X GET \
  http://localhost:8080/api/v1/supplies/groups/year \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc5NywiZXhwIjoxNTU0NjUwOTk3fQ._Z0ccRYjehEJNgkQP9-l0xJu2wj5IhNcix3PWvmIOWI'
```

**Response Example**

```json
[
    {
        "year": 2005,
        "total_amount": 48016,
        "detail_amount": {
            "농협은행/수협은행": 1486,
            "하나은행": 3122,
            "우리은행": 2303,
            "국민은행": 13231,
            "신한은행": 1815,
            "주택도시기금": 22247,
            "외환은행": 1732,
            "기타은행": 1376,
            "한국시티은행": 704
        }
    },
    {
        "year": 2006,
        "total_amount": 41210,
        "detail_amount": {
            "농협은행/수협은행": 2299,
            "하나은행": 3443,
            "우리은행": 4134,
            "국민은행": 5811,
            "신한은행": 1198,
            "주택도시기금": 20789,
            "외환은행": 2187,
            "기타은행": 1061,
            "한국시티은행": 288
        }
    }
    
    ...
]
```



### 7. 연도/기관별 전체 지원금액 중 최대값 조회

각 년도별 각 기관의 전체 지원금액 중에서 가장 큰 금액의 기관를 출력한다.

**URL** : `/api/v1/supplies/groups/year-bank/max`

**Header**: `Authorization: Bearer TOKEN`

**Method** : `GET`

**Reqeust Example**

```shell
curl -X GET \
  http://localhost:8080/api/v1/supplies/groups/year-bank/max \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc5NywiZXhwIjoxNTU0NjUwOTk3fQ._Z0ccRYjehEJNgkQP9-l0xJu2wj5IhNcix3PWvmIOWI'
```

**Response Example**

```json
{
    "name": "주택도시기금",
    "year": 2014
}
```



### 8. 특정 은행의 지원금액 평균 중 최대/최소값 조회

특정 은행의 지원금액 평균 중에서 가장 작은 금액과 큰 금액을 출력하는 API 개발

ex) 외환은행의 경우, 은행코드가 "KEB" 이므로 `/api/v1/banks/KEB/supplies/`로 조회

**URL** : `/api/v1/banks/:bankCode/supplies/avg/min-max`

**Header**: `Authorization: Bearer TOKEN`

**Method** : `GET`

**Reqeust Example**

```shell
curl -X GET \
  http://localhost:8080/api/v1/banks/KEB/supplies/avg/min-max \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwidXNlcm5hbWUiOiJ1c2VyIiwicm9sZXMiOiJST0xFX0FQSSIsImlhdCI6MTU1NDY0Mzc5NywiZXhwIjoxNTU0NjUwOTk3fQ._Z0ccRYjehEJNgkQP9-l0xJu2wj5IhNcix3PWvmIOWI'
```

**Response Example**

```json
{
    "bank": "외환은행",
    "support_amount": [
        {
            "year": 2008,
            "amount": 78
        },
        {
            "year": 2015,
            "amount": 1702
        }
    ]
}
```

