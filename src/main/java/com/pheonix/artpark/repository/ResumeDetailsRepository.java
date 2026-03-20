package com.pheonix.artpark.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeDetailsRepository extends JpaRepository<ResumeDetailsEntity, Long> {
    ResumeDetailsEntity findByUserDetailEntity(UserDetailEntity userDetailEntity);

    boolean existsByUserDetailEntity_Username(String userDetailEntityUsername);

    Slice<ResumeDetailsEntity> findAllBySkillsUpdatedAndExperienceUpdated(boolean skillsUpdated, boolean experienceUpdated, Pageable pageable);
}
