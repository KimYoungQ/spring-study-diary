# My Spring Study Diary

Spring Boot를 이용한 학습 일지 관리 REST API 프로젝트입니다. Layered Architecture와 CRUD 작업을 학습하기 위한 프로젝트입니다.

## 기술 스택

- **Language**: Java 17+
- **Framework**: Spring Boot 4.0.3-SNAPSHOT
- **Database**: MySQL 8.0 (Production), H2 (Test)
- **Build Tool**: Gradle
- **Container**: Docker & Docker Compose

## 프로젝트 구조

```
src/main/java/com/study/my_spring_study_diary/
├── auth/                        # JWT 인증
│   ├── controller/              #   AuthController
│   ├── service/                 #   AuthService, CustomUserDetailsService
│   ├── dao/                     #   UserDao, RefreshTokenDao (MySQL 구현체)
│   ├── dto/                     #   요청/응답 DTO (Login, Signup, TokenRefresh)
│   ├── entity/                  #   User, UserRole
│   └── exception/               #   AuthException, ExpiredTokenException, InvalidTokenException
│
├── study_log/                   # 학습 일지
│   ├── controller/              #   StudyLogController, StudyLogControllerApi (Swagger)
│   ├── service/                 #   StudyLogService
│   ├── dao/                     #   StudyLogDao (MySQL, InMemory 구현체)
│   ├── dto/                     #   요청/응답 DTO (Create, Update, Response, Delete)
│   ├── entity/                  #   StudyLog, Category, Understanding
│   ├── exception/               #   ResourceNotFoundException, DuplicateResourceException, InvalidPageRequestException
│   └── validation/              #   커스텀 Enum 유효성 검증 (EnumValid, EnumValidator)
│
├── discord/                     # Discord 알림
│   ├── controller/              #   DiscordTestController
│   ├── service/                 #   DiscordNotificationService, DiscordWebhookSender (서킷 브레이커 + Rate Limiting)
│   └── dto/                     #   DiscordWebhookMessage
│
├── event/                       # 이벤트 기반 아키텍처
│   ├── auth/                    #   UserRegisteredEvent
│   ├── study/                   #   StudyLogCreatedEvent, StudyGoalAchievedEvent
│   └── handler/                 #   UserRegistrationHandler, StudyLogNotificationHandler, StudyAnalyticsHandler
│
└── global/                      # 전역 설정 및 인프라
    ├── Security/                #   CustomUserDetails, SecurityUtil
    │   ├── config/              #     PasswordEncoderConfig, SecurityConfig
    │   └── jwt/                 #     JwtTokenProvider, JwtAuthenticationFilter, JwtAuthenticationEntryPoint
    ├── common/                  #   ApiResponse (공통 응답), Page (페이지네이션)
    ├── config/                  #   AsyncConfig (스레드 풀), RestClientConfig (HTTP 클라이언트), OpenApiConfig
    ├── exception/               #   GlobalExceptionHandler, BusinessException, ErrorCode
    ├── filter/                  #   MdcLoggingFilter (요청 추적)
    ├── interceptor/             #   LoggingInterceptor (HTTP 요청/응답 로깅)
    └── mapper/                  #   StudyLogMapper (MapStruct)
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

스키마와 초기 데이터는 `src/main/resources/db/schema.sql` 및 `data.sql`로 자동 초기화됩니다.

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
| GET | `/category/{category}/page?page=0&size=10` | 카테고리별 페이징 조회 |
| GET | `/search?title=&category=&startDate=&endDate=&page=0&size=10` | 검색 (페이징 지원) |
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

- **개발 환경**: `application.yaml` (MySQL 사용, 포트 8081)
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

### 인증 및 보안
- JWT 기반 인증 (Access Token 30분 / Refresh Token 7일)
- Spring Security 통합 (Stateless 세션)
- BCrypt 패스워드 암호화

### 데이터 접근
- DAO 패턴 기반 데이터 접근 계층 (Interface + MySQL/InMemory 구현체)
- Spring JDBC를 이용한 데이터 영속성 관리
- 페이징 및 검색 지원

### 이벤트 기반 아키텍처
- Spring ApplicationEvent 활용
- `@TransactionalEventListener` (AFTER_COMMIT 단계)로 트랜잭션 안전성 보장
- 이벤트: UserRegisteredEvent, StudyLogCreatedEvent, StudyGoalAchievedEvent
- 이벤트 체이닝 (학습 분석 핸들러가 목표 달성 이벤트 발행)

### 비동기 처리
- 전용 스레드 풀 구성 (notificationExecutor, analyticsExecutor, asyncExecutor)
- `@Async`를 활용한 비동기 이벤트 핸들링
- 비동기 예외 처리 핸들러

### Discord 알림
- Discord Webhook 연동 (학습 일지 생성/회원가입 시 알림)
- **서킷 브레이커** (Resilience4j): 슬라이딩 윈도우 10회, 실패율 50% 임계값, Open 상태 10초 대기
- **Rate Limiting** (Guava RateLimiter): 초당 0.5회 (최소 2초 간격)

### 로깅 및 모니터링
- MDC 로깅 필터 (requestId, clientIP, method, uri, userId 추적)
- 외부 API HTTP 요청/응답 로깅 인터셉터
- Spring Boot Actuator

### 기타
- RESTful API 설계
- 전역 예외 처리 (`@RestControllerAdvice`)
- Bean Validation을 사용한 요청 데이터 검증 (커스텀 Enum 검증 포함)
- MapStruct를 이용한 Entity-DTO 변환
- Swagger UI API 문서화
- Docker Compose를 통한 데이터베이스 관리
- 환경 변수를 통한 민감한 정보 관리 (.env 파일)

## 데이터베이스 스키마

| 테이블 | 설명 |
|--------|------|
| `study_logs` | 학습 일지 (title, content, category, understanding, study_time, study_date) |
| `users` | 사용자 계정 (email, username, password, role) |
| `refresh_tokens` | JWT Refresh 토큰 저장소 (user_id FK) |

