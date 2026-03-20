package com.pheonix.artpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadMapRepository extends JpaRepository<RoadMapEntity, Long> {
    RoadMapEntity findByResumeDetailsEntity(ResumeDetailsEntity resumeDetailsEntity);

    boolean existsByResumeDetailsEntity_UserDetailEntity_Username(String resumeDetailsEntityUserDetailEntityUsername);
}
