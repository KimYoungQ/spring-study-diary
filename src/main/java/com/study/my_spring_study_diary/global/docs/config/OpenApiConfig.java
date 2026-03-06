package com.study.my_spring_study_diary.global.docs.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 문서 설정
 *
 * 핵심 아이디어: API 문서의 메타데이터(제목, 버전, 설명)와
 * 보안 스키마(JWT)를 중앙에서 관리
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Step 1: API 기본 정보 설정
        Info info = new Info()
                .title("My Spring Study Diary API")
                .version("1.0.0")
                .description("""
                        ## 학습 일지 관리 API 명세서

                        학습 내용을 기록하고 관리하는 REST API입니다.

                        ### 주요 기능
                        - 회원 가입 및 로그인 (JWT 인증)
                        - 학습 기록 CRUD (생성, 조회, 수정, 삭제)
                        - 카테고리별 학습 기록 분류
                        - 이해도 및 학습 시간 추적

                        ### 인증 방법
                        1. `POST /api/auth/signup`으로 회원가입
                        2. `POST /api/auth/login`으로 로그인하여 JWT 토큰 획득
                        3. 우측 상단 **'Authorize'** 버튼 클릭
                        4. `Bearer {토큰값}`을 입력창에 붙여넣기 (Bearer는 자동 추가됨)
                        5. **Authorize** 버튼으로 인증 완료
                        6. 이제 모든 `/api/v1/logs` API 호출 가능
                        """)
                .contact(new Contact()
                        .name("개발팀")
                        .email("dev@studydiary.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        // Step 2: 서버 URL 설정
        Server localServer = new Server()
                .url("http://localhost:8081")
                .description("로컬 개발 서버");

        // Step 3: JWT 보안 스키마 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("JWT Authentication")
                .description("JWT 토큰을 입력하세요. 'Bearer' 접두사는 자동으로 추가됩니다.");

        // Step 4: 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        // Step 5: OpenAPI 객체 조립
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}