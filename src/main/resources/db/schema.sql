-- 데이터베이스가 없으면 생성
CREATE DATABASE IF NOT EXISTS diary_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 사용
USE diary_db;

-- 학습 일지 테이블 생성
CREATE TABLE IF NOT EXISTS study_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '학습 일지 ID',
    title VARCHAR(100) NOT NULL COMMENT '학습 주제',
    content TEXT NOT NULL COMMENT '학습 내용',
    category VARCHAR(50) NOT NULL COMMENT '카테고리 (JAVA, SPRING, DATABASE, ALGORITHM, ETC)',
    understanding VARCHAR(20) NOT NULL COMMENT '이해도 (HIGH, MEDIUM, LOW)',
    study_time INT NOT NULL COMMENT '학습 시간 (분)',
    study_date DATE NOT NULL COMMENT '학습 날짜',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학습 일지 테이블';

-- 인덱스 생성
CREATE INDEX idx_study_logs_category ON study_logs(category);
CREATE INDEX idx_study_logs_study_date ON study_logs(study_date);
CREATE INDEX idx_study_logs_understanding ON study_logs(understanding);
CREATE INDEX idx_study_logs_created_at ON study_logs(created_at);

-- 초기 테스트 데이터 삽입
INSERT INTO study_logs (title, content, category, understanding, study_time, study_date) VALUES
('Spring Boot 시작하기', 'Spring Boot 프로젝트 생성과 기본 설정을 학습했습니다.', 'SPRING', 'VERY_GOOD', 120, '2026-01-26'),
('Java Stream API', 'Stream API를 활용한 함수형 프로그래밍을 학습했습니다.', 'JAVA', 'NORMAL', 90, '2026-01-25'),
('MySQL 인덱스 최적화', '데이터베이스 인덱스 설계와 최적화 방법을 학습했습니다.', 'DATABASE', 'BAD', 60, '2026-01-24'),
('Spring DAO 패턴 구현', 'DAO 패턴을 사용하여 MySQL 데이터베이스를 연동했습니다.', 'SPRING', 'VERY_GOOD', 180, '2026-01-26'),
('Spring Boot Global Response', 'Learned about standardizing API responses with ApiResponse wrapper', 'SPRING', 'GOOD', 150, '2026-01-27'),
('Lombok 적용 완료', 'Spring Boot 프로젝트에 Lombok을 성공적으로 적용했습니다. @Getter, @Setter, @Builder, @Slf4j 등을 활용했습니다.', 'SPRING', 'VERY_GOOD', 180, '2026-01-27');

-- Users 테이블 생성
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
                       email VARCHAR(255) NOT NULL UNIQUE COMMENT '이메일',
                       password VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt)',
                       username VARCHAR(100) NOT NULL COMMENT '사용자명',
                       role VARCHAR(50) NOT NULL DEFAULT 'USER' COMMENT '권한 (USER, ADMIN, MANAGER)',
                       enabled BOOLEAN DEFAULT TRUE COMMENT '활성화 여부',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 테이블';

-- 인덱스 생성
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Refresh Tokens 테이블 생성
CREATE TABLE refresh_tokens (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '토큰 ID',
                                user_id BIGINT NOT NULL COMMENT '사용자 ID',
                                token VARCHAR(500) NOT NULL UNIQUE COMMENT '리프레시 토큰',
                                expires_at TIMESTAMP NOT NULL COMMENT '만료 시간',
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 일시',
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리프레시 토큰 테이블';

-- 인덱스 생성
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- 샘플 사용자 데이터 (비밀번호: password123)
INSERT INTO users (email, password, username, role) VALUES
('admin@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Admin User', 'ADMIN'),
('user@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Normal User', 'USER'),
('manager@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Manager User', 'MANAGER')
ON DUPLICATE KEY UPDATE email = email;

-- 테이블 생성 확인
SELECT 'All tables created successfully with initial data' AS message;