# My Spring Study Diary

Spring Boot를 이용한 학습 일지 관리 REST API 프로젝트입니다. Layered Architecture와 CRUD 작업을 학습하기 위한 프로젝트입니다.

## 기술 스택

- **Language**: Java 17+
- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.0.28 (Production), H2 (Test)
- **ORM**: Spring Data JPA
- **Security**: Spring Security + JWT (JJWT 0.12.6)
- **API 문서**: SpringDoc OpenAPI (Swagger 2.7.0)
- **개발 생산성**: Lombok, MapStruct
- **비동기**: Spring @Async, Event 기반
- **알림**: Discord Webhook 연동
- **Build Tool**: Gradle
- **Container**: Docker & Docker Compose

## 프로젝트 구조

```
src/main/java/com/study/my_spring_study_diary/
├── auth/                    # JWT 인증 (Controller, Service, DAO, DTO, Entity)
├── study_log/               # 학습 일지 (Controller, Service, DAO, DTO, Entity)
├── discord/                 # Discord 알림 (Controller, Service, DTO)
├── event/                   # 이벤트 기반 아키텍처 (Events, Handlers)
└── global/                  # 전역 설정 (Security, Filter, Config, Exception, Common)
```

## 시작하기

### 1. 환경 변수 설정

`.env.example` 파일을 복사하여 `.env` 파일을 생성하고 실제 값으로 수정합니다:

```bash
cp .env.example .env
# .env 파일을 열어서 실제 비밀번호 등을 설정
```

**필수 환경 변수:**
- `JWT_SECRET` - JWT 시크릿 키
- `DISCORD_WEBHOOK_URL` - Discord 웹훅 URL
- `DISCORD_WEBHOOK_ENABLED` - 웹훅 활성화 여부 (true/false)

### 2. 데이터베이스 설정

Docker Compose를 사용하여 MySQL을 실행합니다:

```bash
docker-compose up -d
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

### 4. 테스트 실행

```bash
./gradlew test
```

## API 엔드포인트

### Auth API `/api/auth`

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| POST | `/signup` | 회원가입 |
| POST | `/login` | 로그인 (JWT 발급) |
| POST | `/refresh` | 토큰 갱신 |
| POST | `/logout` | 로그아웃 |

### Study Log API `/api/v1/logs`

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| POST | `/` | 학습 일지 생성 |
| GET | `/` | 전체 학습 일지 조회 |
| GET | `/{id}` | ID로 학습 일지 조회 |
| PUT | `/{id}` | 학습 일지 수정 |
| DELETE | `/{id}` | 학습 일지 삭제 |
| GET | `/date/{date}` | 날짜별 학습 일지 조회 |
| GET | `/category/{category}` | 카테고리별 학습 일지 조회 |
| GET | `/search?title=&category=&startDate=&endDate=` | 검색 |
| GET | `/page?page=0&size=10` | 페이징 조회 |

### Discord API `/api/discord`

| 메서드 | 엔드포인트 | 설명 |
|--------|------------|------|
| POST | `/test` | Discord 웹훅 연동 테스트 |

## API 요청/응답 예시

### 회원가입
```json
POST /api/auth/signup
{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com"
}
```

### 로그인
```json
POST /api/auth/login
{
    "username": "john_doe",
    "password": "password123"
}
```

응답:
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

### 학습 일지 생성
```json
POST /api/v1/logs
Authorization: Bearer {accessToken}
{
    "title": "Spring Security 학습",
    "content": "JWT 인증 방식에 대해 학습했다.",
    "category": "SPRING",
    "understanding": 4
}
```

## Swagger UI

API 문서는 애플리케이션 실행 후 아래 URL에서 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8081/swagger-ui.html

## 프로파일 설정

- **개발 환경**: `application.yml` (MySQL 사용)
- **테스트 환경**: `application-test.yml` (H2 인메모리 DB 사용)

테스트 프로파일로 실행:
```bash
./gradlew bootRun --args='--spring.profiles.active=test'
```

## H2 Console 접속 (테스트 환경)

테스트 프로파일로 실행 후:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: (비워두기)

## 주요 기능

- RESTful API 설계
- JWT 기반 인증 (Access/Refresh 토큰)
- JPA를 사용한 데이터 영속성 관리
- 계층형 아키텍처 (Controller → Service → Repository → Entity)
- 비동기 처리 (전문 스레드 풀)
- 이벤트 기반 아키텍처
- Discord 웹훅 알림 (학습 일지 생성 시)
- MDC 로깅 (요청 추적)
- 전역 예외 처리 (@RestControllerAdvice)
- Bean Validation을 사용한 요청 데이터 검증
- JPA Auditing을 통한 생성/수정 시간 자동 관리
- 페이징 및 정렬 지원
- Swagger UI API 문서화
- Docker Compose를 통한 데이터베이스 관리
- 환경 변수를 통한 민감한 정보 관리 (.env 파일)

## 개발 가이드

자세한 개발 가이드는 [.CLAUDE.md](./.CLAUDE.md) 파일을 참조하세요.
