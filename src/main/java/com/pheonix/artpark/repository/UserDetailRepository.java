package com.pheonix.artpark.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {
    boolean existsByUsername(String username);

    UserDetailEntity findByUsername(String username);
}
