package com.study.my_spring_study_diary.study_log.repository;

import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyLogRepository extends JpaRepository<StudyLog, Long> {

    // Query Method
    List<StudyLog> findByCategory(Category category);

    List<StudyLog> findByUnderstanding(Understanding understanding);

    List<StudyLog> findByStudyDateBetween(LocalDate startDate, LocalDate endDate);

    List<StudyLog> findByTitleContainingIgnoreCase(String keyword);

    // JPQL
    List<StudyLog> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 동적쿼리
    @Query("SELECT s FROM StudyLog s WHERE " +
            "(:title IS NULL OR s.title LIKE %:title%) AND " +
            "(:category IS NULL OR s.category = :category) AND " +
            "(:startDate IS NULL OR s.studyDate >= :startDate) AND " +
            "(:endDate IS NULL OR s.studyDate <= :endDate)")
    List<StudyLog> searchWithConditions(
            @Param("title") String title,
            @Param("category") Category category,
            @Param("StartDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 벌크 업데이트
    @Modifying
    @Query("UPDATE StudyLog s SET s.studyTime = :hours WHERE s.id = :id")
    void updateStudyHours(@Param("id") Long id, @Param("hours") Integer hours);

    // 카운트 쿼리
    long countByCategory(Category category);

    // 날짜별 학습 일지 조회
    List<StudyLog> findByStudyDate(LocalDate date);
}
