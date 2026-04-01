package com.study.my_spring_study_diary.study_log.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("StudyLog DAO 테스트")
class StudyLogDaoTest {

    @Autowired
    private StudyLogDao studyLogDao;

    @Test
    @DisplayName("DAO가 정상적으로 주입된다")
    void daoInjectionTest() {
        assertThat(studyLogDao).isNotNull();
    }

    @Test
    @DisplayName("전체 조회가 가능하다")
    void findAllTest() {
        // When
        var result = studyLogDao.findAll();

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("전체 개수를 조회할 수 있다")
    void countTest() {
        // When
        long count = studyLogDao.count();

        // Then
        assertThat(count).isGreaterThanOrEqualTo(0);
    }
}