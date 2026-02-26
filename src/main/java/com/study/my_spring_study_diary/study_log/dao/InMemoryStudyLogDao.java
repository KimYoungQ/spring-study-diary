package com.study.my_spring_study_diary.study_log.dao;

import com.study.my_spring_study_diary.global.common.Page;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryStudyLogDao implements StudyLogDao {

    // 데이터 저장소 (실제 DB 대신 Map 사용)
    private final Map<Long, StudyLog> database = new HashMap<>();

    // ID 자동 증가를 위한 시퀀스
    private final AtomicLong sequence = new AtomicLong(1);
    private final ContentNegotiatingViewResolver contentNegotiatingViewResolver;

    public InMemoryStudyLogDao(ContentNegotiatingViewResolver contentNegotiatingViewResolver) {
        this.contentNegotiatingViewResolver = contentNegotiatingViewResolver;
    }

//    @PostConstruct
//    public void init() {
//        System.out.println("🚀 InMemoryDB 커넥션 완료!");
//    }

    // ========== CREATE ==========
    /**
     * 학습 일지 저장
     *
     * @param studyLog 저장할 학습 일지
     * @return 저장된 학습 일지 (ID 포함)
     */
    @Override
    public StudyLog save(StudyLog studyLog) {
        // ID가 없으면 새로운 ID 부여
        if (studyLog.getId() == null) {
            studyLog.setId(sequence.getAndIncrement());
        }

        //Map에 저장
        database.put(studyLog.getId(), studyLog);

        return studyLog;
    }

    // ========== READ ==========
    /**
     * ID로 학습 일지 조회
     */
    @Override
    public Optional<StudyLog> findById(Long id) {
        return Optional.ofNullable(database.get(id));
    }

    /**
     * 전체 학습 일지 조회 (최신순 정렬)
     */
    @Override
    public List<StudyLog> findAll() {
        return database.values().stream()
                .sorted(Comparator.comparing(StudyLog::getCreatedAt))
                //.sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyLog> findByCategory(String category) {
        try {
            Category categoryEnum = Category.valueOf(category.toUpperCase());
            return findByCategory(categoryEnum);
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }

    /**
     * 카테고리 학습 일지 조회
     */
    private List<StudyLog> findByCategory(Category category) {
        return database.values().stream()
                .filter(log -> log.getCategory().equals(category))
                .sorted(Comparator.comparing(StudyLog::getCreatedAt))
                .collect(Collectors.toList());
    }
    /**
     * 날짜로 학습 일지 조회
     */
    @Override
    public List<StudyLog> findByStudyDate(LocalDate date) {
        return database.values().stream()
                .filter(log -> log.getStudyDate().equals(date))
                .sorted(Comparator.comparing(StudyLog::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return database.containsKey(id);
    }

    @Override
    public long count() {
        return database.size();
    }

    // ========== UPDATE ==========

    /**
     * 학습 일지 수정 (Update)
     * Map은 같은 키로 put하면 덮어쓰므로 save와 동일하게 동작
     * 하지만 의미를 명확히 하기 위해 별도 메서드로 분리
     */
    @Override
    public StudyLog update(StudyLog studyLog) {
        validationStudyLogById(studyLog);

        // updatedAt 갱신
        studyLog.setUpdatedAt(java.time.LocalDateTime.now());

        database.put(studyLog.getId(), studyLog);
        return studyLog;
    }

    /**
     * studyLog의 ID 값 검증
     */
    private void validationStudyLogById(StudyLog studyLog) {
        if (studyLog.getId() == null) {
            throw new IllegalArgumentException("수정할 학습 일지의 ID가 없습니다.");
        }

        if (!database.containsKey(studyLog.getId())) {
            throw new IllegalArgumentException(
                    "해당 학습 일지를 찾을 수 없습니다. (id: " + studyLog.getId() + ")"
            );
        }
    }

    @Override
    public Page<StudyLog> searchWithPaging(String titleKeyword, String category, LocalDate startDate, LocalDate endDate, int page, int size) {

        List<StudyLog> filteredLogs = database.values().stream()
                .filter(log -> {
                    boolean matches = true;

                    // 제목 키워드 필터
                    if (titleKeyword != null && !titleKeyword.isBlank()) {
                        matches = log.getTitle().contains(titleKeyword);
                    }

                    // 카테고리 필터
                    if (matches && category != null && !category.isBlank()) {
                        matches = log.getCategory().name().equals(category);
                    }

                    // 시작 날짜 필터
                    if (matches && startDate != null) {
                        matches = !log.getStudyDate().isBefore(startDate);
                    }

                    // 종료 날짜 필터
                    if (matches && endDate != null) {
                        matches = !log.getStudyDate().isAfter(endDate);
                    }

                    return matches;
                })
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .collect(Collectors.toList());

        int totalElements = filteredLogs.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        if (fromIndex >= totalElements) {
            return new Page<>(new ArrayList<>(), page, size, totalElements);
        }

        List<StudyLog> content = filteredLogs.subList(fromIndex, toIndex);
        return new Page<>(content, page, size, totalElements);
    }

    // ========== DELETE ==========
    @Override
    public boolean deleteById(Long id) {
        // Map.remove()는 삭제된 값을 반환, 없으면 null 반환
        StudyLog removed = database.remove(id);
        return removed != null;
    }

    @Override
    public void deleteAll() {
        database.clear();
        // 테스트 용도로 시퀀스도 초기화
        sequence.set(1);
    }

    /**
     * 페이징 처리된 학습 일지 조회
     *
     * @param page 현재 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return 페이징 처리된 결과
     */
    public Page<StudyLog> findAllWithPaging(int page, int size) {

        // 1.전체 데이터를 정렬
        List<StudyLog> allLogs = database.values().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .collect(Collectors.toList());

        int totalElements = allLogs.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        if (fromIndex >= totalElements) {
            return new Page<>(new ArrayList<>(), page, size, totalElements);
        }

        List<StudyLog> content = allLogs.subList(fromIndex, toIndex);
        return new Page<>(content, page, size, totalElements);
    }

    /**
     * 카테고리별 페이징 조회
     *
     * @param category    카테고리
     * @param pageRequest 페이징 요청 정보
     * @return 페이징 처리된 결과
     */
    public Page<StudyLog> findByCategoryWithPaging(String Category, int page, int size) {

        // 1. 카테코리로 필터링 및 정렬
        List<StudyLog> categoryLogs = database.values().stream()
                .filter(log -> log.getCategory().name().equals(Category))
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .collect(Collectors.toList());

        int totalElements = categoryLogs.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        if (fromIndex >= totalElements) {
            return new Page<>(new ArrayList<>(), page, size, totalElements);
        }

        List<StudyLog> content = categoryLogs.subList(fromIndex, toIndex);
        return new Page<>(content, page, size, totalElements);
    }

    /**
     * 총 페이지 수 계산
     * @param totalElements 전체 데이터 개수
     * @param pageSize      페이지 크기
     * @return 총 페이지 수
     */
    private int calculateTotalPages(long totalElements, int pageSize) {
        return (int) Math.ceil((double) totalElements / pageSize);
    }

}
