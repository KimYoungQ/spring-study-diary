package com.study.my_spring_study_diary.study_log.dao;

import com.study.my_spring_study_diary.global.common.Page;
import com.study.my_spring_study_diary.study_log.entity.Category;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import com.study.my_spring_study_diary.study_log.entity.Understanding;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Primary  // 이 구현체를 기본으로 사용
public class MySQLStudyLogDaoImpl implements StudyLogDao {

    private final JdbcTemplate jdbcTemplate;

    public MySQLStudyLogDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ========== CREATE ==========
    public StudyLog save(StudyLog studyLog) {
        String sql = """
            INSERT INTO study_logs (title, content, category, understanding, study_time, study_date)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        // KeyHolder: Object to receive auto-generated ID
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, studyLog.getTitle());
            ps.setString(2, studyLog.getContent());
            ps.setString(3, studyLog.getCategory().name());
            ps.setString(4, studyLog.getUnderstanding().name());
            ps.setInt(5, studyLog.getStudyTime());
            ps.setDate(6, Date.valueOf(studyLog.getStudyDate()));
            return ps;
        }, keyHolder);

        // Set the generated ID to StudyLog object
        Number generatedId = keyHolder.getKey();
        if (generatedId != null) {
            studyLog.setId(generatedId.longValue());
        }

        return studyLog;
    }

    @Override
    public Optional<StudyLog> findById(Long id) {
        String sql = "SELECT * FROM study_logs WHERE id = ?";

        try {
            StudyLog studyLog = jdbcTemplate.queryForObject(sql, studyLogRowMapper, id);
            return Optional.ofNullable(studyLog);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<StudyLog> findAll() {
        String sql = "SELECT * FROM study_logs ORDER BY study_date DESC, id DESC";
        return jdbcTemplate.query(sql, studyLogRowMapper);
    }

    @Override
    public List<StudyLog> findByCategory(String category) {
        String sql = "SELECT * FROM study_logs WHERE category = ? ORDER BY study_date DESC, id DESC";
        return jdbcTemplate.query(sql, studyLogRowMapper, category);
    }

    @Override
    public List<StudyLog> findByStudyDate(LocalDate date) {
        String sql = "SELECT * FROM study_logs WHERE study_date = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, studyLogRowMapper, Date.valueOf(date));
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM study_logs WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM study_logs";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }

    // ========== UPDATE ==========

    @Override
    public StudyLog update(StudyLog studyLog) {
        String sql = """
                UPDATE study_logs
                SET title = ?, content = ?, category = ?, understanding = ?,
                    study_time = ?, study_date = ?
                WHERE id = ?
                """;

        int updated = jdbcTemplate.update(sql,
                studyLog.getTitle(),
                studyLog.getContent(),
                studyLog.getCategory().name(),
                studyLog.getUnderstanding().name(),
                studyLog.getStudyTime(),
                studyLog.getStudyDate(),
                studyLog.getId());

        if (updated == 0) {
            throw new RuntimeException("Study log not found. ID: " + studyLog.getId());
        }

        return studyLog;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = """
                DELETE FROM study_logs WHERE id = ?
                """;
        int deleted = jdbcTemplate.update(sql,id);
        return deleted > 0;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM study_logs";
        jdbcTemplate.update(sql);
    }

    // ========== PAGING ==========

    @Override
    public Page<StudyLog> findAllWithPaging(int page, int size) {
        // 1단계: 전체 개수 조회
        String countSql = "SELECT COUNT(*) FROM study_logs";
        Long totalElements = jdbcTemplate.queryForObject(countSql, Long.class);

        // 전체 데이터가 0건이면 빈 페이지 반환
        if (totalElements == null || totalElements == 0) {
            return new Page<>(List.of(), page, size, 0);
        }

        // 2단계: 해당 페이지 데이터 조회
        String dataSql = """
                SELECT * FROM study_logs
                ORDER BY study_date DESC, id DESC
                LIMIT ? OFFSET ?
                """;

        int offset = page * size;
        List<StudyLog> content = jdbcTemplate.query(dataSql, studyLogRowMapper, size, offset);

        // 3단계: Page 객체 생성 및 반환
        return new Page<>(content, page, size, totalElements);
    }

    @Override
    public Page<StudyLog> findByCategoryWithPaging(String category, int page, int size) {
        // 1단계: 전체 개수 조회
        String countSql = "SELECT COUNT(*) FROM study_logs";
        Long totalElements = jdbcTemplate.queryForObject(countSql, Long.class);

        // 전체 데이터가 0건이면 빈 페이지 반환
        if (totalElements == null || totalElements == 0) {
            return new Page<>(List.of(), page, size, 0);
        }

        // 2단계: 해당 페이지 데이터 조회
        String dataSql = """
            SELECT * FROM study_logs
            ORDER BY study_date DESC, id DESC
            LIMIT ? OFFSET ?
            """;

        int offset = page * size;
        List<StudyLog> content = jdbcTemplate.query(dataSql, studyLogRowMapper, size, offset);

        // 3단계: Page 객체 생성 및 반환
        return new Page<>(content, page, size, totalElements);
    }

    @Override
    public Page<StudyLog> searchWithPaging(
            String titleKeyword,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size) {

        // 공통 WHERE 절 구성
        StringBuilder whereClause = new StringBuilder("WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (titleKeyword != null && !titleKeyword.isBlank()) {
            whereClause.append(" AND title LIKE ?");
            params.add("%" + titleKeyword + "%");
        }

        if (category != null && !category.isBlank()) {
            whereClause.append(" AND category = ?");
            params.add(category);
        }

        if (startDate != null) {
            whereClause.append(" AND study_date >= ?");
            params.add(Date.valueOf(startDate));
        }

        if (endDate != null) {
            whereClause.append(" AND study_date <= ?");
            params.add(Date.valueOf(endDate));
        }

        // 1단계: COUNT 쿼리 (WHERE 절 재사용)
        String countSql = "SELECT COUNT(*) FROM study_logs " + whereClause;
        Long totalElements = jdbcTemplate.queryForObject(countSql, Long.class, params.toArray());

        if (totalElements == null || totalElements == 0) {
            return new Page<>(List.of(), page, size, 0);
        }

        // 2단계: 데이터 쿼리 (WHERE 절 재사용 + 페이징)
        String dataSql = "SELECT * FROM study_logs "
                + whereClause
                + " ORDER BY study_date DESC, id DESC"
                + " LIMIT ? OFFSET ?";

        // 페이징 파라미터를 기존 파라미터에 추가
        List<Object> dataParams = new ArrayList<>(params);
        dataParams.add(size);
        dataParams.add(page * size);

        List<StudyLog> content = jdbcTemplate.query(
                dataSql, studyLogRowMapper, dataParams.toArray());

        return new Page<>(content, page, size, totalElements);
    }

    // ========== PRIVATE METHODS ==========

    /**
     * RowMapper: Converts each row of ResultSet to StudyLog object
     * Can be simply implemented with lambda expression
     */
    private final RowMapper<StudyLog> studyLogRowMapper = (rs, rowNum) -> {
        StudyLog studyLog = new StudyLog();
        studyLog.setId(rs.getLong("id"));
        studyLog.setTitle(rs.getString("title"));
        studyLog.setContent(rs.getString("content"));
        studyLog.setCategory(Category.valueOf(rs.getString("category")));
        studyLog.setUnderstanding(Understanding.valueOf(rs.getString("understanding")));
        studyLog.setStudyTime(rs.getInt("study_time"));
        studyLog.setStudyDate(rs.getDate("study_date").toLocalDate());
        return studyLog;
    };
}
