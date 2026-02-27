package com.study.my_spring_study_diary.auth.dao;

import com.study.my_spring_study_diary.auth.entity.User;
import com.study.my_spring_study_diary.study_log.entity.StudyLog;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserDao {

    // ========== CREATE ==========
    User save(User user);

    // ========== READ ==========
    Optional<User> existsByUserName(String userName);

    Optional<User> existsByEmail(String email);

}
