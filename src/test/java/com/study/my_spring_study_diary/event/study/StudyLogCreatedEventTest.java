package com.study.my_spring_study_diary.event.study;

import com.study.my_spring_study_diary.event.handler.StudyLogNotificationHandler;
import com.study.my_spring_study_diary.study_log.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import com.study.my_spring_study_diary.study_log.service.StudyLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.task.execution.pool.core-size=2",
        "spring.task.execution.pool.max-size=4"
})
class StudyLogEventTest {

    @Autowired
    private StudyLogService studyLogService;

    @MockitoSpyBean
    private StudyLogNotificationHandler notificationHandler;

    @Test
    void 학습로그_생성시_비동기_알림이_발송된다() {
        // given
        StudyLogCreateRequest request = StudyLogCreateRequest.builder()
                .title("Spring Boot 비동기 처리 학습")
                .content("@Async와 이벤트 처리를 공부했습니다")
                .category(Category.SPRING.name())
                .understanding(Understanding.GOOD.name())
                .studyTime(120)
                .studyDate(LocalDate.now())
                .build();

        // when
        studyLogService.createStudyLog(request);

        // then
        // 비동기 처리이므로 Awaitility로 검증
        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(notificationHandler, timeout(5000))
                            .handleStudyLogCreated(any(StudyLogCreatedEvent.class));
                });
    }
}