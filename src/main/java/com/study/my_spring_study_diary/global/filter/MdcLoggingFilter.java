package com.study.my_spring_study_diary.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/*
 *MDC 필터: 모든 HTTP 요청에 고유 ID를 부여하는 서블릿 필터
 *
 * 동작 흐름:
 * 1. 요청이 들어옴
 * 2. 고유한 requestId 생성
 * 3. MDC에 requestId 저장
 * 4. 요청 처리 (컨트롤러 → 서비스 → 리포지토리)
 *    → 이 동안 모든 log.xxx() 호출에 requestId가 자동 포함
 * 5. 요청 처리 완료
 * 6. MDC 정리 (메모리 누수 방지)
 */
@Slf4j
public class MdcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        try {
            // 1. 고유 요청 ID 생성
            // 클라이언트가 X-Request-ID 헤더를 보냈으면 그것을 사용
            // 없으면 새로 생성 (UUID의 앞 8자리)
            String requestId = httpRequest.getHeader("X-Request-ID");
            if (requestId == null || requestId.isBlank()) {
                requestId = UUID.randomUUID().toString().substring(0, 8);
            }

            // 2. MDC에 데이터 저장
            // MDC.put(key, value): 현재 스레드의 MDC Map에 데이터 저장
            // 이 스레드에서 출력되는 모든 로그에 자동으로 포함됨
            MDC.put("requestId", requestId);
            MDC.put("clientIP", getClientIP(httpRequest));
            MDC.put("method", httpRequest.getMethod());
            MDC.put("uri", httpRequest.getRequestURI());

            // 로그인한 사용자 정보가 있다면 추가
            String userId = getUserIdFromRequest();
            if (userId != null) {
                MDC.put("userId", userId);
            }

            log.debug("요청 시작");

            // 3. 다음 필터 또는 컨트롤러로 요청 전달
            chain.doFilter(httpRequest, response);

            log.debug("요청 완료");

        } finally {
            // 4. MDC 정리 (매우 중요!)
            // 웹 서버는 스레드를 재사용하므로,
            // 정리하지 않으면 다른 요청에 이전 요청의 정보가 남아있게 됨
            MDC.clear();
        }
    }

    private String getClientIP(HttpServletRequest request) {
        // 프록시/로드밸런서를 거친 경우 실제 클라이언트 IP 추출
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getUserIdFromRequest() {
        // SecurityContextHolder에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();  // username 반환
        }

        return null;
    }
}
