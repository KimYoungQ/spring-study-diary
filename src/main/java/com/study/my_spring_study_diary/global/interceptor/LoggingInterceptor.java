package com.study.my_spring_study_diary.global.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * HTTP 요청/응답 로깅 인터셉터
 *
 * ClientHttpRequestInterceptor는 RestClient/RestTemplate의 모든 요청에
 * 공통 로직을 적용할 수 있는 인터셉터 인터페이스
 *
 * 동작 흐름:
 * 1. 요청 전: 요청 정보 로깅
 * 2. 실제 요청 실행 (execution.execute)
 * 3. 응답 후: 응답 정보 로깅
 */
@Slf4j
@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        // ─── 1. 요청 로깅 (execution 실행 전) ───────────────────────
        logRequest(request, body);

        long startTime = System.currentTimeMillis();

        // ─── 2. 실제 HTTP 요청 실행 ──────────────────────────────────
        ClientHttpResponse response = execution.execute(request, body);

        // ✅ Body를 버퍼에 복사하여 여러 번 읽기 가능하게 래핑
        BufferedClientHttpResponse bufferedResponse = new BufferedClientHttpResponse(response);

        // ─── 3. 응답 로깅 (execution 실행 후) ───────────────────────
        long duration = System.currentTimeMillis() - startTime;
        logResponse(response, duration);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("========== HTTP Request ==========");
        log.info("URI     : {}", request.getURI());
        log.info("Method  : {}", request.getMethod());
        log.info("Headers : {}", request.getHeaders());

        if (body.length > 0) {
            log.info("Body    : {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(ClientHttpResponse response, long duration) throws IOException {
        log.info("========== HTTP Response ==========");
        log.info("Status  : {} {}", response.getStatusCode().value(),
                response.getStatusCode());
        log.info("Headers : {}", response.getHeaders());
        log.info("Duration: {}ms", duration);

        // Body는 InputStream이라 한 번 읽으면 소진됨 → BufferingClientHttpResponse 필요
        byte[] responseBody = response.getBody().readAllBytes();
        if (responseBody.length > 0) {
            log.info("Body    : {}", new String(responseBody, StandardCharsets.UTF_8));
        }
    }

    // Body를 byte[]로 캐싱하는 래퍼 클래스
    private static class BufferedClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse original;
        private final byte[] cachedBody;

        public BufferedClientHttpResponse(ClientHttpResponse response) throws IOException {
            this.original = response;
            this.cachedBody = response.getBody().readAllBytes(); // 한 번만 읽어 캐싱
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(cachedBody); // 매번 새 InputStream 반환
        }

        @Override public HttpStatusCode getStatusCode() throws IOException { return original.getStatusCode(); }
        @Override public String getStatusText() throws IOException { return original.getStatusText(); }
        @Override public HttpHeaders getHeaders() { return original.getHeaders(); }
        @Override public void close() { original.close(); }
    }
}
