-- 학습 일지 테스트 데이터 (H2 Local 환경용)
-- 카테고리: JAVA, SPRING, JPA, DATABASE, ALGORITHM, CS, NETWORK, GIT, ETC
-- 이해도: VERY_GOOD, GOOD, NORMAL, BAD, VERY_BAD

-- 2026년 1월 데이터 (Java 중심)
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('Java 기초 - 변수와 데이터 타입', '기본형과 참조형 변수의 차이점을 학습했습니다. Stack과 Heap 메모리 구조를 이해했습니다.', 'JAVA', 'VERY_GOOD', 90, '2026-01-01'),
('Java 컬렉션 프레임워크 - List', 'ArrayList와 LinkedList의 내부 구조와 성능 차이를 학습했습니다. 언제 어떤 것을 사용해야 하는지 이해했습니다.', 'JAVA', 'GOOD', 120, '2026-01-02'),
('Java Stream API 기초', 'filter, map, reduce 등 기본 Stream 연산을 학습했습니다. 함수형 프로그래밍의 장점을 이해했습니다.', 'JAVA', 'NORMAL', 150, '2026-01-03'),
('Java 동시성 프로그래밍', 'Thread와 Runnable의 차이점, synchronized 키워드 사용법을 학습했습니다. 동시성 이슈가 발생하는 이유를 이해했습니다.', 'JAVA', 'BAD', 180, '2026-01-04'),
('Java Optional 클래스', 'NullPointerException을 방지하는 Optional 사용법을 학습했습니다. orElse와 orElseGet의 차이점을 이해했습니다.', 'JAVA', 'VERY_GOOD', 60, '2026-01-05');

-- 2026년 1월 데이터 (Spring 중심)
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('Spring Boot 시작하기', 'Spring Initializr로 프로젝트 생성, 의존성 관리, application.yml 설정을 학습했습니다.', 'SPRING', 'VERY_GOOD', 120, '2026-01-06'),
('Spring IoC 컨테이너', '의존성 주입(DI)의 개념과 @Component, @Service, @Repository 애노테이션을 학습했습니다.', 'SPRING', 'GOOD', 90, '2026-01-07'),
('Spring MVC 구조', 'DispatcherServlet, HandlerMapping, ViewResolver의 동작 원리를 학습했습니다.', 'SPRING', 'NORMAL', 150, '2026-01-08'),
('Spring Boot REST API', '@RestController, @RequestMapping, HTTP 메서드 매핑을 학습했습니다. RESTful API 설계 원칙을 이해했습니다.', 'SPRING', 'VERY_GOOD', 180, '2026-01-09'),
('Spring AOP 기초', 'Aspect, Pointcut, Advice의 개념을 학습했습니다. 로깅과 트랜잭션 관리에 AOP를 적용해봤습니다.', 'SPRING', 'BAD', 210, '2026-01-10');

-- 2026년 1월 데이터 (JPA 중심)
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('JPA Entity 매핑', '@Entity, @Table, @Column 애노테이션을 사용한 엔티티 매핑을 학습했습니다.', 'JPA', 'GOOD', 120, '2026-01-11'),
('JPA 연관관계 매핑', '@OneToMany, @ManyToOne 양방향 연관관계 설정과 주의사항을 학습했습니다.', 'JPA', 'NORMAL', 180, '2026-01-12'),
('JPQL과 QueryDSL', 'JPQL 기본 문법과 QueryDSL을 사용한 타입 세이프한 쿼리 작성법을 학습했습니다.', 'JPA', 'BAD', 150, '2026-01-13'),
('JPA N+1 문제 해결', 'Fetch Join과 @EntityGraph를 사용한 N+1 문제 해결 방법을 학습했습니다.', 'JPA', 'VERY_BAD', 240, '2026-01-14'),
('JPA 영속성 컨텍스트', '1차 캐시, 변경 감지, 지연 로딩의 동작 원리를 학습했습니다.', 'JPA', 'NORMAL', 90, '2026-01-15');

-- 2026년 1월 데이터 (Database 중심)
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('MySQL 인덱스 최적화', 'B-Tree 인덱스 구조와 인덱스 설계 전략을 학습했습니다. EXPLAIN 명령어로 쿼리 실행 계획을 분석했습니다.', 'DATABASE', 'GOOD', 150, '2026-01-16'),
('트랜잭션 격리 수준', 'READ UNCOMMITTED, READ COMMITTED, REPEATABLE READ, SERIALIZABLE의 차이점을 학습했습니다.', 'DATABASE', 'NORMAL', 120, '2026-01-17'),
('데이터베이스 정규화', '제1정규형부터 제3정규형까지의 정규화 과정을 학습했습니다. 역정규화가 필요한 경우도 이해했습니다.', 'DATABASE', 'VERY_GOOD', 90, '2026-01-18'),
('SQL 튜닝 기초', '느린 쿼리 찾기, 인덱스 활용, 서브쿼리 최적화 방법을 학습했습니다.', 'DATABASE', 'GOOD', 180, '2026-01-19'),
('NoSQL vs RDBMS', 'MongoDB와 MySQL의 차이점, CAP 이론, 각각의 사용 케이스를 학습했습니다.', 'DATABASE', 'NORMAL', 60, '2026-01-20');

-- 2026년 1월 데이터 (Algorithm 중심)
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('시간복잡도와 공간복잡도', 'Big-O 표기법을 이용한 알고리즘 성능 분석 방법을 학습했습니다.', 'ALGORITHM', 'VERY_GOOD', 90, '2026-01-21'),
('정렬 알고리즘 비교', 'Quick Sort, Merge Sort, Heap Sort의 구현과 성능 비교를 학습했습니다.', 'ALGORITHM', 'GOOD', 150, '2026-01-22'),
('이진 탐색 트리', 'BST의 삽입, 삭제, 검색 연산과 균형 트리의 필요성을 학습했습니다.', 'ALGORITHM', 'NORMAL', 120, '2026-01-23'),
('동적 계획법 기초', '피보나치 수열을 예제로 Memoization과 Tabulation 기법을 학습했습니다.', 'ALGORITHM', 'BAD', 180, '2026-01-24'),
('그래프 알고리즘 - DFS/BFS', '깊이 우선 탐색과 너비 우선 탐색의 구현과 활용 사례를 학습했습니다.', 'ALGORITHM', 'VERY_BAD', 210, '2026-01-25');

-- 2026년 1월 말 ~ 2월 초 데이터 (다양한 카테고리)
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('JVM 메모리 구조', 'Heap, Stack, Method Area, PC Register, Native Method Stack의 역할을 학습했습니다.', 'CS', 'GOOD', 120, '2026-01-26'),
('가비지 컬렉션 이해하기', 'Young Generation과 Old Generation, GC 알고리즘의 종류를 학습했습니다.', 'CS', 'NORMAL', 90, '2026-01-27'),
('HTTP 프로토콜 심화', 'HTTP/1.1과 HTTP/2의 차이점, Keep-Alive, 파이프라이닝을 학습했습니다.', 'NETWORK', 'VERY_GOOD', 150, '2026-01-28'),
('TCP 3-way Handshake', 'TCP 연결 수립과 종료 과정, 패킷 흐름을 학습했습니다.', 'NETWORK', 'GOOD', 60, '2026-01-29'),
('Git Rebase vs Merge', 'Rebase와 Merge의 차이점, 각각의 장단점과 사용 시나리오를 학습했습니다.', 'GIT', 'NORMAL', 90, '2026-01-30'),
('Git 브랜치 전략', 'Git Flow, GitHub Flow, GitLab Flow의 차이점과 적용 사례를 학습했습니다.', 'GIT', 'VERY_GOOD', 120, '2026-01-31');

-- 2026년 2월 데이터
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('Spring Security 기초', 'Authentication과 Authorization의 차이, Security Filter Chain을 학습했습니다.', 'SPRING', 'BAD', 180, '2026-02-01'),
('JWT 토큰 인증 구현', 'Access Token과 Refresh Token을 사용한 인증 시스템을 구현했습니다.', 'SPRING', 'NORMAL', 240, '2026-02-02'),
('Redis 캐싱 전략', 'Look Aside, Write Through, Write Behind 캐싱 전략을 학습했습니다.', 'DATABASE', 'GOOD', 150, '2026-02-03'),
('Docker 컨테이너화', 'Dockerfile 작성, 이미지 빌드, 컨테이너 실행 방법을 학습했습니다.', 'ETC', 'VERY_GOOD', 120, '2026-02-04'),
('Kubernetes 기초', 'Pod, Service, Deployment의 개념과 kubectl 명령어를 학습했습니다.', 'ETC', 'VERY_BAD', 300, '2026-02-05'),
('마이크로서비스 아키텍처', 'Monolithic vs MSA, API Gateway, Service Mesh의 개념을 학습했습니다.', 'ETC', 'BAD', 180, '2026-02-06'),
('Spring Boot 페이징 구현', 'JdbcTemplate을 사용한 Offset 기반 페이징과 Cursor 기반 페이징을 구현했습니다.', 'SPRING', 'VERY_GOOD', 210, '2026-02-07'),
('Lombok 라이브러리 활용', '@Data, @Builder, @Slf4j 등 Lombok 애노테이션으로 보일러플레이트 코드를 줄였습니다.', 'SPRING', 'GOOD', 90, '2026-02-08'),
('MapStruct 매퍼 적용', 'Entity와 DTO 간 변환을 MapStruct로 자동화했습니다. 컴파일 타임 매핑의 장점을 이해했습니다.', 'SPRING', 'NORMAL', 120, '2026-02-09'),
('Spring Boot 예외 처리', '@ControllerAdvice와 @ExceptionHandler로 전역 예외 처리를 구현했습니다.', 'SPRING', 'VERY_GOOD', 150, '2026-02-10'),
('API 응답 표준화', 'ApiResponse 래퍼 클래스로 일관된 API 응답 구조를 구현했습니다.', 'SPRING', 'GOOD', 60, '2026-02-11');
