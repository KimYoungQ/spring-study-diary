package com.study.my_spring_study_diary.mapper;

import com.study.my_spring_study_diary.dto.request.StudyLogCreateRequest;
import com.study.my_spring_study_diary.dto.request.StudyLogUpdateRequest;
import com.study.my_spring_study_diary.dto.response.StudyLogResponse;
import com.study.my_spring_study_diary.entity.StudyLog;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct Mapper Interface for StudyLog
 *
 * MapStruct를 사용한 Entity-DTO 변환
 * 컴파일 시점에 구현체가 자동 생성됨
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudyLogMapper {

    /**
     * StudyLog Entity → StudyLogResponse DTO 변환
     *
     * @param studyLog Entity
     * @return DTO
     */
    @Mapping(target = "categoryIcon", expression = "java(studyLog.getCategory().getIcon())")
    @Mapping(target = "understandingEmoji", expression = "java(studyLog.getUnderstanding().getEmoji())")
    StudyLogResponse toResponse(StudyLog studyLog);

    /**
     * StudyLogCreateRequest DTO → StudyLog Entity 변환
     *
     * @param request DTO
     * @return Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", expression = "java(com.study.my_spring_study_diary.entity.Category.valueOf(request.getCategory()))")
    @Mapping(target = "understanding", expression = "java(com.study.my_spring_study_diary.entity.Understanding.valueOf(request.getUnderstanding()))")
    StudyLog toEntity(StudyLogCreateRequest request);

    /**
     * StudyLog Entity 리스트 → StudyLogResponse DTO 리스트 변환
     *
     * @param studyLogs Entity 리스트
     * @return DTO 리스트
     */
    List<StudyLogResponse> toResponseList(List<StudyLog> studyLogs);

    /**
     * StudyLogUpdateRequest의 값으로 기존 StudyLog Entity 업데이트
     *
     * @param request 업데이트 요청 DTO
     * @param studyLog 업데이트할 기존 Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", expression = "java(request.getCategory() != null ? com.study.my_spring_study_diary.entity.Category.valueOf(request.getCategory()) : studyLog.getCategory())")
    @Mapping(target = "understanding", expression = "java(request.getUnderstanding() != null ? com.study.my_spring_study_diary.entity.Understanding.valueOf(request.getUnderstanding()) : studyLog.getUnderstanding())")
    void updateEntityFromRequest(StudyLogUpdateRequest request, @MappingTarget StudyLog studyLog);

    /**
     * 부분 업데이트를 위한 커스텀 메서드
     * null이 아닌 필드만 업데이트
     *
     * @param request 업데이트 요청 DTO
     * @param entity 업데이트할 기존 Entity
     */
    default void partialUpdate(StudyLogUpdateRequest request, StudyLog entity) {
        if (request.getTitle() != null) {
            entity.updateTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            entity.updateContent(request.getContent());
        }
        if (request.getCategory() != null) {
            entity.updateCategory(com.study.my_spring_study_diary.entity.Category.valueOf(request.getCategory()));
        }
        if (request.getUnderstanding() != null) {
            entity.updateUnderstanding(com.study.my_spring_study_diary.entity.Understanding.valueOf(request.getUnderstanding()));
        }
        if (request.getStudyTime() != null) {
            entity.updateStudyTime(request.getStudyTime());
        }
        if (request.getStudyDate() != null) {
            entity.updateStudyDate(request.getStudyDate());
        }
    }
}